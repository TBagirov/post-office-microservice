package org.bagirov.subscriptionservice.dto.request

import java.util.*

data class SubscriptionRequest (
    val publicationId: UUID,
    val duration: Int
)