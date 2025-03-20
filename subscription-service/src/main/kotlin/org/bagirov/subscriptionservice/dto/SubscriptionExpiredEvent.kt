package org.bagirov.subscriptionservice.dto

import org.bagirov.subscriptionservice.props.NotificationType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Событие об истечении подписки")
data class SubscriptionExpiredEvent(
    @Schema(description = "Email получателя уведомления")
    override val email: String,

    @Schema(description = "Название издания, подписка на которое истекла")
    val publicationName: String,

    @Schema(description = "Дата истечения подписки (строка)")
    val expirationDate: String
) : NotificationEvent {
    @Schema(description = "Тип уведомления: SUBSCRIPTION_EXPIRED")
    override val type = NotificationType.SUBSCRIPTION_EXPIRED
}