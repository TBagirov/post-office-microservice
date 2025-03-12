package org.bagirov.subscriptionservice.dto

import org.bagirov.subscriptionservice.props.NotificationType


// Истечение подписки
data class SubscriptionExpiredEvent(
    override val email: String,
    val publicationName: String,
    val expirationDate: String
) : NotificationEvent {
    override val type = NotificationType.SUBSCRIPTION_EXPIRED
}