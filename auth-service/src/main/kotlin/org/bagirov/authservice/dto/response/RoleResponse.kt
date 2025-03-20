package org.bagirov.authservice.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO, описывающее роль пользователя")
data class RoleResponse(
    @Schema(description = "ID роли")
    val id: UUID,

    @Schema(description = "Название роли (ADMIN, GUEST, POSTMAN, SUBSCRIBER, ...)")
    val name: String
)