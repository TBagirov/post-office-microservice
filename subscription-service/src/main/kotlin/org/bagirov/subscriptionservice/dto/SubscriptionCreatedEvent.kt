package org.bagirov.subscriptionservice.dto

import java.util.*

data class SubscriptionCreatedEvent(
    val subscriptionId: UUID,
    val subscriberId: UUID,
    val publicationId: UUID,
    val duration: Int
)