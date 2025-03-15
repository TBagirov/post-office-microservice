package org.bagirov.subscriptionservice.dto

import org.bagirov.subscriptionservice.props.SubscriptionStatus
import java.util.*
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO для события оплаты подписки")
data class SubscriptionPaymentEvent(
    @Schema(description = "ID подписки")
    val subscriptionId: UUID,

    @Schema(description = "Новый статус подписки после попытки оплаты")
    val status: SubscriptionStatus
)