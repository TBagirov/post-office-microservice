package org.bagirov.subscriberservice.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO c деталями о подписчике (subscriber-service)")
data class SubscriberResponse(
    @Schema(description = "ID записи в таблице subscribers")
    val id: UUID,

    @Schema(description = "ID пользователя (из auth-service)")
    val userId: UUID,

    @Schema(description = "Здание (дом)")
    val building: String,

    @Schema(description = "Доп. информация об адресе (кв. и т.д.)")
    val subAddress: String?,

    @Schema(description = "ID улицы (streetId)")
    val streetId: UUID,

    @Schema(description = "ID района (districtId)")
    val districtId: UUID,
)