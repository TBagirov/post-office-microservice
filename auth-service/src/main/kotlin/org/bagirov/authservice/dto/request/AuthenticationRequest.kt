package org.bagirov.authservice.dto.request


import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO для авторизации (логина) пользователя")
data class AuthenticationRequest(
    @Schema(description = "Логин (username)")
    var username: String,

    @Schema(description = "Пароль пользователя")
    var password: String
)