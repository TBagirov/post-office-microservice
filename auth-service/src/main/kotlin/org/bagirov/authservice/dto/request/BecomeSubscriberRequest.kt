package org.bagirov.authservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "DTO для преобразования пользователя GUEST в SUBSCRIBER")
data class BecomeSubscriberRequest(
    @Schema(description = "Название улицы (streetName)")
    val streetName: String,

    @Schema(
        description = "Здание (дом), не более 5 символов",
        example = "123"
    )
    @field:Size(min=1, max = 5, message = "Длина building не может превышать 5 символов")
    val building: String,

    @Schema(
        description = "Доп. информация об адресе (квартира и т.д.), не более 5 символов",
        example = "Apt1"
    )
    @field:Size(min=1, max = 5, message = "Длина subAddress не может превышать 5 символов")
    val subAddress: String?
)