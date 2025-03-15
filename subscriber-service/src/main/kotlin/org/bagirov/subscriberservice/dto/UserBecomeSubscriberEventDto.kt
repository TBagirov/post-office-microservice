package org.bagirov.subscriberservice.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Событие, отправляемое при превращении пользователя в подписчика")
data class UserBecomeSubscriberEventDto(
    @Schema(description = "ID пользователя")
    val userId: UUID,

    @Schema(description = "ID улицы (streetId)")
    val streetId: UUID,

    @Schema(description = "ID района (districtId)")
    val districtId: UUID,

    @Schema(description = "Здание (дом)")
    val building: String,

    @Schema(description = "Доп. информация об адресе (кв. и т.п.)")
    val subAddress: String?,

    @Schema(description = "Дата/время создания (millis)")
    val createdAt: Long
)