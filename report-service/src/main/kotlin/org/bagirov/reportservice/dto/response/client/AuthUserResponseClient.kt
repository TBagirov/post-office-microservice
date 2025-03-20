package org.bagirov.reportservice.dto.response.client

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO с информацией о пользователе, полученной из auth-service")
data class AuthUserResponseClient(
    @Schema(description = "ID пользователя")
    val userId: UUID,

    val surname: String,
    val name: String,
    val patronymic: String,

    @Schema(description = "Email пользователя")
    val email: String,

    @Schema(description = "Логин пользователя (username)")
    val username: String
)