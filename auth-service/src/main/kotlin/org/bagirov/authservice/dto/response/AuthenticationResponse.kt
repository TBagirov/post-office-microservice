package org.bagirov.authservice.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO, возвращаемое при авторизации/регистрации (содержит токен)")
data class AuthenticationResponse(
    @Schema(description = "ID пользователя")
    var id: UUID,

    @Schema(description = "Логин (username) пользователя")
    var username: String,

    @Schema(description = "JWT-токен доступа")
    var accessToken: String
)