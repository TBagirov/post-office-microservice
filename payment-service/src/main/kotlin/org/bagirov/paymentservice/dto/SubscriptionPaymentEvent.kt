package org.bagirov.paymentservice.dto

import org.bagirov.paymentservice.props.SubscriptionStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO для события оплаты (payment -> subscription)")
data class SubscriptionPaymentEvent(
    @Schema(description = "ID подписки")
    val subscriptionId: UUID,

    @Schema(description = "Статус подписки после оплаты (ACTIVE/CANCELLED и т.д.)")
    val status: SubscriptionStatus
)