package org.bagirov.authservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO для преобразования пользователя GUEST в SUBSCRIBER")
data class BecomeSubscriberRequest(
    @Schema(description = "Название улицы (streetName)")
    val streetName: String,

    @Schema(description = "Здание (дом)")
    val building: String,

    @Schema(description = "Доп. информация об адресе (квартира и т.д.)")
    val subAddress: String?
)