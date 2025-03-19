package org.bagirov.reportservice.dto

import org.bagirov.reportservice.props.SubscriptionStatus
import java.util.*

data class SubscriptionUpdatedEvent(
    val subscriptionId: UUID,
    val newStatus: SubscriptionStatus
)
