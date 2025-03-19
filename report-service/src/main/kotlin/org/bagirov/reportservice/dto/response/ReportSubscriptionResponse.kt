package org.bagirov.reportservice.dto.response


import org.bagirov.reportservice.props.SubscriptionStatus
import java.time.LocalDateTime
import java.util.*

data class ReportSubscriptionResponse (
    val subscriptionId: UUID,
    val subscriberId: UUID,
    val publicationId: UUID,
    val fioSubscriber: String,
    val titlePublication: String,
    val startDateSubscription: LocalDateTime,
    val endDateSubscription: LocalDateTime,
    val statusSubscription: SubscriptionStatus
)