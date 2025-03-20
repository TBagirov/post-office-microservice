package org.bagirov.subscriberservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO для обновления данных подписчика")
data class SubscriberUpdateRequest(
    @Schema(description = "Номер здания (дом)")
    val building: String,

    @Schema(description = "Доп. информация (квартира и т.д.)")
    val subAddress: String?,

    @Schema(description = "Название улицы (streetName)")
    val streetName: String
)