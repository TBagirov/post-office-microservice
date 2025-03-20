package org.bagirov.subscriptionservice.dto

import org.bagirov.subscriptionservice.props.NotificationType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Событие отмены подписки")
data class SubscriptionCancelledEvent(
    @Schema(description = "Email подписчика")
    override val email: String,

    @Schema(description = "Название издания")
    val publicationName: String,

    @Schema(description = "Причина отмены подписки")
    val cancellationReason: String
) : NotificationEvent {
    @Schema(description = "Тип уведомления: SUBSCRIPTION_CANCELLED")
    override val type = NotificationType.SUBSCRIPTION_CANCELLED
}