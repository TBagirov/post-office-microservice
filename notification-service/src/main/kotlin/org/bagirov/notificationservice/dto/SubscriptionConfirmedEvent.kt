package org.bagirov.notificationservice.dto

import org.bagirov.notificationservice.props.NotificationType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Событие подтверждения подписки (notification-service)")
data class SubscriptionConfirmedEvent(
    @Schema(description = "Email подписчика")
    override val email: String,

    @Schema(description = "Имя (username) подписчика")
    val username: String,

    @Schema(description = "Название издания")
    val publicationName: String,

    @Schema(description = "Дата начала подписки (строка)")
    val startDate: String,

    @Schema(description = "Продолжительность (в месяцах)")
    val duration: Int
) : NotificationEvent {
    @Schema(description = "Тип уведомления: SUBSCRIPTION_CONFIRMED")
    override val type = NotificationType.SUBSCRIPTION_CONFIRMED
}