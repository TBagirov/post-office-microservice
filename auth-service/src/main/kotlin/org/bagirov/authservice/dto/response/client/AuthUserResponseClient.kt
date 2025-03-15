package org.bagirov.authservice.dto.response.client

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO для передачи сведений о пользователе из auth-service")
data class AuthUserResponseClient(
    @Schema(description = "ID пользователя")
    val userId: UUID,

    @Schema(description = "Email пользователя")
    val email: String,

    @Schema(description = "Имя пользователя (username)")
    val username: String
)