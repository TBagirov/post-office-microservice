package org.bagirov.reportservice.dto

import java.util.*
import io.swagger.v3.oas.annotations.media.Schema
import org.bagirov.reportservice.props.SubscriptionStatus
import java.time.LocalDateTime

@Schema(description = "Событие о создании подписки")
data class SubscriptionCreatedEvent(
    @Schema(description = "ID подписки")
    val subscriptionId: UUID,

    @Schema(description = "ID подписчика (subscriberId)")
    val subscriberId: UUID,

    @Schema(description = "ID издания (publicationId)")
    val publicationId: UUID,

    @Schema(description = "Продолжительность подписки (в месяцах)")
    val duration: Int,

    @Schema(description = "Дата начала подписки")
    val startDate: LocalDateTime,

    @Schema(description = "Дата окончания подписки")
    val endDate: LocalDateTime,

    @Schema(description = "Статус подписки")
    val status: SubscriptionStatus
)
