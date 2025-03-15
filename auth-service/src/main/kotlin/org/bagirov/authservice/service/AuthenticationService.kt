package org.bagirov.authservice.service

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.jsonwebtoken.JwtException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.bagirov.authservice.client.PostalServiceClient
import org.bagirov.authservice.dto.UserBecomeSubscriberEventDto
import org.bagirov.authservice.dto.request.AuthenticationRequest
import org.bagirov.authservice.dto.request.BecomeSubscriberRequest
import org.bagirov.authservice.dto.request.RegistrationRequest
import org.bagirov.authservice.dto.response.AuthenticationResponse
import org.bagirov.authservice.entity.RefreshTokenEntity
import org.bagirov.authservice.entity.UserEntity
import org.bagirov.authservice.props.Role
import org.bagirov.authservice.repository.RefreshTokenRepository
import org.bagirov.authservice.repository.RoleRepository
import org.bagirov.authservice.repository.UserRepository
import org.bagirov.authservice.utill.convertToResponseEventDto
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val kafkaProducerService: KafkaProducerService,
    // Feign-клиент для запроса данных улицы
    private val postalServiceClient: PostalServiceClient
) {

    private val log = KotlinLogging.logger {}

    @Transactional
    fun authorization(request: AuthenticationRequest, response: HttpServletResponse): AuthenticationResponse {
        log.info { "Starting authorization process for user: ${request.username}" }
        if (!isValidAuthenticationCredentials(request)) {
            log.warn { "Invalid authentication credentials provided" }
            throw IllegalArgumentException("Поля логин и/или пароль пустые")
        }

        val user = userRepository.findByUsername(request.username)
            .orElseThrow {
                log.error { "User ${request.username} not found" }
                NoSuchElementException("пользователь не зарегистрирован")
            }

        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))
        log.info { "User ${request.username} authenticated successfully" }

        val accessToken = jwtService.createAccessToken(user)
        val refreshToken = jwtService.createRefreshToken(user)

        refreshTokenRepository.save(RefreshTokenEntity(user = user, token = refreshToken))
        log.info { "Generated new refresh token for user: ${request.username}" }

        setRefreshTokenInCookie(response, refreshToken)
        return AuthenticationResponse(accessToken = accessToken, username = user.username, id = user.id!!)
    }

    @Transactional
    fun registration(request: RegistrationRequest, response: HttpServletResponse, roleName: String = "GUEST"): AuthenticationResponse {
        log.info { "Starting registration process for user: ${request.username}" }

        // все проверки, если не выбросилось ни одно исключение, значит можно регистрировать
        validRequestRegistration(request)

        val role = roleRepository.findByName(roleName)!!
        val user = UserEntity(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            role = role,
            name = request.name,
            surname = request.surname,
            patronymic = request.patronymic,
            email = request.email,
            phone = request.phone,
            createdAt = LocalDateTime.now()
        )

        userRepository.save(user)
        log.info { "User ${request.username} registered successfully" }

        val accessToken = jwtService.createAccessToken(user)
        val refreshToken = jwtService.createRefreshToken(user)

        setRefreshTokenInCookie(response, refreshToken)
        refreshTokenRepository.save(RefreshTokenEntity(user = user, token = refreshToken))
        log.info { "Generated refresh token for newly registered user: ${request.username}" }

        kafkaProducerService.sendUserCreatedEvent(user.convertToResponseEventDto())

        log.info { "Sent Kafka event for user registration: ${request.username}" }

        return AuthenticationResponse(accessToken = accessToken, username = user.username, id = user.id!!)
    }

    @Transactional
    @CircuitBreaker(name = "subscriberService", fallbackMethod = "fallbackUpdateSubscriber")
    fun becomeSubscriber(currentUser: UserEntity, request: BecomeSubscriberRequest) {

        // Проверяем длину полей building и subAddress
        if (request.building.length > 5) {
            throw IllegalArgumentException("Значение 'building' не может быть длиннее 5 символов")
        }
        if ((request.subAddress?.length ?: 0) > 5) {
            throw IllegalArgumentException("Значение 'subAddress' не может быть длиннее 5 символов")
        }

        // Ищем пользователя
        val user = userRepository.findById(currentUser.id!!)
            .orElseThrow { IllegalArgumentException("Запрос от несуществующего пользователя") }


        val roleSubscriber = roleRepository.findByName(Role.SUBSCRIBER)
            ?: throw NoSuchElementException("Роли ${Role.SUBSCRIBER} нет в базе данных!")

        // Запрашиваем у PostalService `streetId` и `districtId`
        val postalData = postalServiceClient.getStreetAndDistrict(request.streetName)

        // Обновляем роль на SUBSCRIBER
        user.role = roleSubscriber
        userRepository.save(user)

        // Отправляем событие в Kafka
        val event = UserBecomeSubscriberEventDto(
            userId = user.id!!,
            streetId = postalData.streetId,
            districtId = postalData.districtId,
            building = request.building,
            subAddress = request.subAddress,
            createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
        )

        kafkaProducerService.sendUserBecameSubscriberEvent(event)
    }
    // fallback метод, если PostalService недоступен
    fun fallbackUpdateSubscriber(user: UserEntity, request: BecomeSubscriberRequest, ex: Throwable) {
        log.error("Circuit Breaker activated for postal-service! Reason: ${ex.message}", ex)
        throw IllegalStateException("Circuit Breaker: Subscriber Service is currently unavailable: ${ex.message}. Please try again later.")
    }

    fun logout(token: String, response: HttpServletResponse): Map<String, String> {
        log.info { "Logging out user with refresh token: $token" }
        refreshTokenRepository.findAllByToken(token).forEach { refreshTokenRepository.delete(it) }

        val cookie = Cookie("refreshToken", null)
        cookie.maxAge = 0
        cookie.path = "/"
        response.addCookie(cookie)
        log.info { "Cleared refresh token cookie" }

        return mapOf("message" to "Logout successful")
    }

    @Transactional
    fun refresh(userToken: String, response: HttpServletResponse): AuthenticationResponse {
        log.info { "Refreshing token for user token: $userToken" }
        if (userToken.isEmpty()) {
            log.warn { "Token is empty" }
            throw JwtException("Token is empty")
        }

        val username = jwtService.getUsername(userToken)
        val user = userRepository.findByUsername(username).orElseThrow {
            log.error { "User not found for token: $userToken" }
            NoSuchElementException("Пользователя с таким username(${username}) больше нет")
        }

        if (!refreshTokenRepository.findAll().any { it.token == userToken }) {
            log.warn { "Invalid refresh token: $userToken" }
            throw JwtException("Token not valid")
        }

        val accessToken = jwtService.createAccessToken(user)
        val refreshToken = jwtService.createRefreshToken(user)
        refreshTokenRepository.save(RefreshTokenEntity(user = user, token = refreshToken))
        log.info { "Generated new access and refresh tokens for user: ${user.username}" }

        setRefreshTokenInCookie(response, refreshToken)
        return AuthenticationResponse(accessToken = accessToken, username = user.username, id = user.id!!)
    }

    private fun validRequestRegistration(request: RegistrationRequest){

        if (!isValidRegistrationCredentials(request)) {
            log.warn { "Invalid registration credentials provided" }
            throw IllegalArgumentException("Заполнены не все данные!!!")
        }

        if (!isValidEmail(request.email)) {
            log.warn { "Invalid email format: ${request.email}" }
            throw IllegalArgumentException("Некорректный формат email")
        }

        if (!isValidPhone(request.phone)) {
            log.warn { "Invalid phone format: ${request.phone}" }
            throw IllegalArgumentException("Некорректный формат номера телефона (Ожидаемый формат: +7XXXXXXXXXX)")
        }

        if (userRepository.existsByUsername(request.username)) {
            log.warn { "User with username ${request.username} already exists" }
            throw IllegalArgumentException("Пользователь с таким username уже существует")
        }

        if (userRepository.existsByEmail(request.email)) {
            log.warn { "User with email ${request.email} already exists" }
            throw IllegalArgumentException("Пользователь с таким email уже существует")
        }

        if (userRepository.existsByPhone(request.phone)) {
            log.warn { "User with phone ${request.phone} already exists" }
            throw IllegalArgumentException("Пользователь с таким phone уже существует")
        }

    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }

    private fun isValidPhone(phone: String): Boolean {
        val phoneRegex = "^\\+7\\d{10}$".toRegex()
        return phone.matches(phoneRegex)
    }


    fun setRefreshTokenInCookie(response: HttpServletResponse, token: String) {
        val cookie = ResponseCookie.from("refreshToken", token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofDays(30))
            .sameSite("None")
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        log.info { "Set refresh token in cookie" }
    }

    private fun isValidAuthenticationCredentials(request: AuthenticationRequest) =
        request.username.isNotEmpty() && request.password.isNotEmpty()

    private fun isValidRegistrationCredentials(request: RegistrationRequest) =
        request.username.isNotEmpty() && request.password.isNotEmpty() && request.name.isNotEmpty()
}
