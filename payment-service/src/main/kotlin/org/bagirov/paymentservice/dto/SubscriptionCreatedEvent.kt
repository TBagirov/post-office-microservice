package org.bagirov.paymentservice.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Событие, получаемое payment-сервисом при создании подписки")
data class SubscriptionCreatedEvent(
    @Schema(description = "ID подписки")
    val subscriptionId: UUID,

    @Schema(description = "ID подписчика (subscriberId)")
    val subscriberId: UUID,

    @Schema(description = "ID публикации (издания)")
    val publicationId: UUID,

    @Schema(description = "Продолжительность подписки (месяцы)")
    val duration: Int
)