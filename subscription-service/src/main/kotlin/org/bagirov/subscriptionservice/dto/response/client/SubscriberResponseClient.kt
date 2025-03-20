package org.bagirov.subscriptionservice.dto.response.client

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO с деталями о подписчике, полученными из subscriber-service")
data class SubscriberResponseClient(
    @Schema(description = "ID записи в таблице subscribers")
    val id: UUID,

    @Schema(description = "ID пользователя (в auth-service)")
    val userId: UUID,

    @Schema(description = "Номер здания (дом)")
    val building: String,

    @Schema(description = "Дополнительное поле адреса (квартира и т.д.)")
    val subAddress: String?,

    @Schema(description = "ID улицы (streetId)")
    val streetId: UUID,

    @Schema(description = "ID района (districtId)")
    val districtId: UUID
)