package org.bagirov.subscriptionservice.dto.response

import java.time.LocalDateTime
import java.util.*

data class SubscriptionResponse (
    val id: UUID,
    val subscriberId: UUID,
    val publicationId: UUID,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val duration: Int,
)