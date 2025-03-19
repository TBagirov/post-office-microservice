package org.bagirov.subscriptionservice.dto

import org.bagirov.subscriptionservice.props.SubscriptionStatus
import java.util.*

data class SubscriptionUpdatedEvent(
    val subscriptionId: UUID,
    val newStatus: SubscriptionStatus
)
