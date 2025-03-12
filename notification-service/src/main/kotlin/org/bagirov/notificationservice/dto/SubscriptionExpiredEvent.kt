package org.bagirov.notificationservice.dto

import org.bagirov.notificationservice.props.NotificationType


// Истечение подписки
data class SubscriptionExpiredEvent(
    override val email: String,
    val publicationName: String,
    val expirationDate: String
) : NotificationEvent {
    override val type = NotificationType.SUBSCRIPTION_EXPIRED
}