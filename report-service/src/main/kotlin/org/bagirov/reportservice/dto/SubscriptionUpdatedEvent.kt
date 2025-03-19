package org.bagirov.reportservice.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.bagirov.reportservice.props.SubscriptionStatus
import java.util.*

/**
 * DTO для события обновления подписки.
 */
@Schema(description = "DTO с информацией о событии обновления подписки")
data class SubscriptionUpdatedEvent(

    @Schema(description = "ID подписки", example = "550e8400-e29b-41d4-a716-446655440000")
    val subscriptionId: UUID,

    @Schema(description = "Новый статус подписки", example = "ACTIVE")
    val newStatus: SubscriptionStatus
)
