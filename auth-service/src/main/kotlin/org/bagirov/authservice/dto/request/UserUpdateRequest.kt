package org.bagirov.authservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO для обновления персональных данных пользователя")
data class UserUpdateRequest(
    @Schema(description = "Новое имя пользователя")
    val name: String?,

    @Schema(description = "Новая фамилия пользователя")
    val surname: String?,

    @Schema(description = "Новое отчество пользователя")
    val patronymic: String?,

    @Schema(description = "Новый email пользователя")
    val email: String?,

    @Schema(description = "Новый телефон пользователя")
    val phone: String?
)