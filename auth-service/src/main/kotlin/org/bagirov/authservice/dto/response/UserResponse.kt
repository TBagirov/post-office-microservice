package org.bagirov.authservice.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.*

@Schema(description = "DTO с информацией о пользователе (AuthService)")
data class UserResponse(
    @Schema(description = "Уникальный идентификатор пользователя")
    val id: UUID,

    @Schema(description = "Имя пользователя")
    val name: String,

    @Schema(description = "Фамилия пользователя")
    val surname: String,

    @Schema(description = "Отчество пользователя")
    val patronymic: String,

    @Schema(description = "Логин (username)")
    val username: String,

    @Schema(description = "Пароль (захешированный)")
    val password: String,

    @Schema(description = "Электронная почта пользователя")
    val email: String,

    @Schema(description = "Телефон пользователя")
    val phone: String,

    @Schema(description = "Дата создания")
    val createdAt: LocalDateTime,

    @Schema(description = "Дата обновления (может быть null)")
    val updatedAt: LocalDateTime?,

    @Schema(description = "Роль (ADMIN, GUEST, SUBSCRIBER, POSTMAN и т.д.)")
    val role: String
)