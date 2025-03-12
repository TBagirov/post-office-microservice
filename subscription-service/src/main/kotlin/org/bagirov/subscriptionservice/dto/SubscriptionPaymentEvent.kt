package org.bagirov.subscriptionservice.dto

import org.bagirov.subscriptionservice.props.SubscriptionStatus
import java.util.*

data class SubscriptionPaymentEvent(
    val subscriptionId: UUID,
    val status: SubscriptionStatus
)