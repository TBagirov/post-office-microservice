package org.bagirov.subscriptionservice.dto

import java.util.*
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Событие о создании подписки")
data class SubscriptionCreatedEvent(
    @Schema(description = "ID подписки")
    val subscriptionId: UUID,

    @Schema(description = "ID подписчика (subscriberId)")
    val subscriberId: UUID,

    @Schema(description = "ID издания (publicationId)")
    val publicationId: UUID,

    @Schema(description = "Продолжительность подписки (в месяцах)")
    val duration: Int
)