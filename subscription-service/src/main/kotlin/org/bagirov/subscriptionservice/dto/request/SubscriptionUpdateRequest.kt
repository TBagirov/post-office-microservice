package org.bagirov.subscriptionservice.dto.request

import java.util.*

data class SubscriptionUpdateRequest (
    val id: UUID,
    val publicationId: UUID,
    val duration: Int
)