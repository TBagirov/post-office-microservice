package org.bagirov.notificationservice.dto

import org.bagirov.notificationservice.props.NotificationType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Событие отмены подписки (notification-service)")
data class SubscriptionCancelledEvent(
    @Schema(description = "Email подписчика")
    override val email: String,

    @Schema(description = "Название издания")
    val publicationName: String,

    @Schema(description = "Причина отмены")
    val cancellationReason: String
) : NotificationEvent {
    @Schema(description = "Тип уведомления: SUBSCRIPTION_CANCELLED")
    override val type = NotificationType.SUBSCRIPTION_CANCELLED
}