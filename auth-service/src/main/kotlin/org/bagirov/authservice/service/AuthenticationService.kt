package org.bagirov.authservice.service


import io.jsonwebtoken.JwtException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.bagirov.authservice.dto.AuthenticationRequest
import org.bagirov.authservice.dto.AuthenticationResponse
import org.bagirov.authservice.dto.RegistrationRequest
import org.bagirov.authservice.entity.RefreshTokenEntity
import org.bagirov.authservice.entity.UserEntity
import org.bagirov.authservice.repository.RefreshTokenRepository
import org.bagirov.authservice.repository.RoleRepository
import org.bagirov.authservice.repository.UserRepository
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
    private val refreshTokenRepository: RefreshTokenRepository
) {

    @Transactional
    fun authorization(request: AuthenticationRequest, response: HttpServletResponse): AuthenticationResponse {
        if (!isValidAuthenticationCredentials(request)) {
            throw IllegalArgumentException("Поля логин и/или пароль пустые")
        }

        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(request.username, request.password))

        val user = userRepository.findByUsername(request.username)
            .orElseThrow { throw NoSuchElementException("пользователя не существует") }

        val accessToken = jwtService.createAccessToken(user)
        val refreshToken = jwtService.createRefreshToken(user)

        val refreshTokenEntity = RefreshTokenEntity(
            user = user,
            token = refreshToken
        )
        refreshTokenRepository.save(refreshTokenEntity)

        setRefreshTokenInCookie(response, refreshToken)

        return AuthenticationResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            username = user.username,
            id = user.id!!
        )
    }

    @Transactional
    fun registration(request: RegistrationRequest, response: HttpServletResponse): AuthenticationResponse {
        if (!isValidRegistrationCredentials(request)) {
            throw IllegalArgumentException("Заполнены не все данные!!!")
        }

        val users = userRepository.findAll()

        users.forEach { user ->
            if (request.username == user.username) {
                throw IllegalArgumentException("Пользователь с таким username уже существует")
            }
        }

        val roleGuest = roleRepository
            .findByName("GUEST")

        val user = UserEntity(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            role = roleGuest!!,
            name = request.name,
            surname = request.surname,
            patronymic = request.patronymic,
            email = request.email,
            phone = request.phone,
            createdAt = LocalDateTime.now()
        )

        userRepository.save(user)

        val accessToken = jwtService.createAccessToken(user)
        val refreshToken = jwtService.createRefreshToken(user)

        setRefreshTokenInCookie(response, refreshToken)

        val refreshTokenEntity = RefreshTokenEntity(
            user = user,
            token = refreshToken
        )
        refreshTokenRepository.save(refreshTokenEntity)

        return AuthenticationResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            username = user.username,
            id = user.id!!
        )
    }

    fun logout(token: String, response: HttpServletResponse): Map<String, String> {
        val refreshTokensEntity = refreshTokenRepository.findAllByToken(token)
        refreshTokensEntity.forEach { refreshToken ->
            refreshTokenRepository.delete(refreshToken)
        }

        val cookie = Cookie("refreshToken", null)
        cookie.maxAge = 0
        cookie.path = "/"
        response.addCookie(cookie)

        return mapOf("message" to "Logout successful")
    }

    @Transactional
    fun refresh(userToken: String, response: HttpServletResponse): AuthenticationResponse {
        if (userToken.isEmpty()) {
            throw JwtException("Token is empty")
        }

        val username = jwtService.getUsername(userToken)
        val user = userRepository.findByUsername(username)
            .orElseThrow() { throw NoSuchElementException("Пользователя с таким username(${username}) больше нет") }

        var isValidToken = false

        val refreshTokens: MutableList<RefreshTokenEntity> = refreshTokenRepository.findAll()
        for (token in refreshTokens) {
            if (token.token == userToken) {
                isValidToken = true
            }
        }

        if (!isValidToken) {
            throw JwtException("Token not valid")
        }

        val accessToken = jwtService.createAccessToken(user)
        val refreshToken = jwtService.createRefreshToken(user)

        userRepository.save(user)

        val refreshTokenEntity = RefreshTokenEntity(
            user = user,
            token = refreshToken
        )

        refreshTokenRepository.save(refreshTokenEntity)
        setRefreshTokenInCookie(response, refreshToken)

        return AuthenticationResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            username = user.username,
            id = user.id!!
        )
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
    }

    private fun isValidAuthenticationCredentials(request: AuthenticationRequest) =
        request.username.isNotEmpty() && request.password.isNotEmpty()

    private fun isValidRegistrationCredentials(request: RegistrationRequest) =
        request.username.isNotEmpty() && request.password.isNotEmpty() && request.name.isNotEmpty()

}