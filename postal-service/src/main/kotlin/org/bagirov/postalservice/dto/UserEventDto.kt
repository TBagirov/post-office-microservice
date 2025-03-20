package org.bagirov.postalservice.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.*

@Schema(description = "DTO, описывающее событие при создании пользователя (postal-service)")
data class UserEventDto(
    @Schema(description = "ID пользователя")
    val id: UUID,

    @Schema(description = "Дата создания пользователя")
    val createdAt: LocalDateTime,

    @Schema(description = "Роль пользователя")
    val role: String
)