package org.bagirov.authservice.controller

import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.bagirov.authservice.dto.request.AuthenticationRequest
import org.bagirov.authservice.dto.response.AuthenticationResponse
import org.bagirov.authservice.dto.request.RegistrationRequest
import org.bagirov.authservice.props.Role
import org.bagirov.authservice.service.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/authorization")
    @Operation(summary = "Авторизация пользователя")
    fun authorization(@RequestBody request: AuthenticationRequest, response: HttpServletResponse):
            ResponseEntity<AuthenticationResponse>
    {
        logger.info {"Request to authorization ${request.username}"}
        return ResponseEntity.ok(authenticationService.authorization(request, response))
    }

    @PostMapping("/registration")
    @Operation(summary = "Регистрация пользователя")
    fun registration(@RequestBody request: RegistrationRequest, response: HttpServletResponse):
            ResponseEntity<AuthenticationResponse>
    {
        logger.info("Request to registration ${request.username}")
        return ResponseEntity.ok(authenticationService.registration(request, response))
    }

    @PostMapping("/registration-postman")
    @Operation(summary = "Регистрация пользователя")
    fun registrationPostman(@RequestBody request: RegistrationRequest, response: HttpServletResponse):
            ResponseEntity<AuthenticationResponse>
    {
        logger.info("Request to registration postman ${request.username}")
        return ResponseEntity.ok(authenticationService.registration(request, response, Role.POSTMAN))
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход пользователя с сайта")
    fun logout(@CookieValue(value = "refreshToken") token: String, response: HttpServletResponse):
            ResponseEntity<Map<String, String>>
    {
        logger.info("Request to logout")
        return ResponseEntity.ok(authenticationService.logout(token, response))
    }

    @GetMapping("/refresh")
    @Operation(summary = "Обновление токена")
    fun refresh(@CookieValue(value = "refreshToken") token: String, response: HttpServletResponse):
            ResponseEntity<AuthenticationResponse>
    {
        logger.info {"Request to refresh"}
        return ResponseEntity.ok(authenticationService.refresh(token, response))
    }



}