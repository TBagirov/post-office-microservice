package org.bagirov.reportservice.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.*

/**
 * DTO для события обновления пользователя.
 */
@Schema(description = "DTO с информацией о событии обновления пользователя")
data class UserUpdatedEventDto(

    @Schema(description = "ID пользователя", example = "550e8400-e29b-41d4-a716-446655440000")
    val userId: UUID,

    @Schema(description = "Имя пользователя", example = "Иван")
    val name: String,

    @Schema(description = "Фамилия пользователя", example = "Иванов")
    val surname: String,

    @Schema(description = "Отчество пользователя", example = "Иванович")
    val patronymic: String,

    @Schema(description = "Email пользователя", example = "ivanov@example.com")
    val email: String,

    @Schema(description = "Телефон пользователя", example = "+7 999 123-45-67")
    val phone: String,

    @Schema(description = "Дата последнего обновления в формате HH:mm, dd/MM/yyyy", example = "14:30, 10/03/2024")
    val updatedAt: LocalDateTime?,

    @Schema(description = "Роль пользователя", example = "SUBSCRIBER")
    val role: String
)
