package org.bagirov.reportservice.dto.response


import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class ReportSubscriptionByIdSubscriberResponse (
    val subscriptionId: UUID,
    val publicationId: UUID,
    val title: String,
    val type: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val price: BigDecimal
)