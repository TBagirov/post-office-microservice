package org.bagirov.authservice.service

import io.jsonwebtoken.JwtException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.bagirov.authservice.dto.request.AuthenticationRequest
import org.bagirov.authservice.dto.response.AuthenticationResponse
import org.bagirov.authservice.dto.request.RegistrationRequest
import org.bagirov.authservice.entity.RefreshTokenEntity
import org.bagirov.authservice.entity.UserEntity
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

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val kafkaProducerService: KafkaProducerService
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
        if (!isValidRegistrationCredentials(request)) {
            log.warn { "Invalid registration credentials provided" }
            throw IllegalArgumentException("Заполнены не все данные!!!")
        }

        if (userRepository.findAll().any { it.username == request.username }) {
            log.warn { "User with username ${request.username} already exists" }
            throw IllegalArgumentException("Пользователь с таким username уже существует")
        }

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
