package org.bagirov.authservice.dto.request


import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO для регистрации нового пользователя")
data class RegistrationRequest(
    @Schema(description = "Имя (не username)")
    val name: String,

    @Schema(description = "Фамилия пользователя")
    val surname: String,

    @Schema(description = "Отчество пользователя")
    val patronymic: String,

    @Schema(description = "Логин (username)")
    val username: String,

    @Schema(description = "Пароль")
    val password: String,

    @Schema(
        description = "Электронная почта",
        example = "test@test.com"
    )
    val email: String,

    @Schema(
        description = "Номер телефона",
        example = "+71234567890"
    )
    val phone: String,
)