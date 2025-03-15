package org.bagirov.authservice.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.*

@Schema(description = "Событие создания пользователя (AuthService)")
data class UserEventDto(
    @Schema(description = "ID пользователя")
    val id: UUID,

    @Schema(description = "Дата создания пользователя")
    val createdAt: LocalDateTime,

    @Schema(description = "Роль пользователя (строка)")
    val role: String
)
