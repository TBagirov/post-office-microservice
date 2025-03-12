package org.bagirov.paymentservice.dto

import org.bagirov.paymentservice.props.SubscriptionStatus
import java.util.*

data class SubscriptionPaymentEvent(
    val subscriptionId: UUID,
    val status: SubscriptionStatus
)