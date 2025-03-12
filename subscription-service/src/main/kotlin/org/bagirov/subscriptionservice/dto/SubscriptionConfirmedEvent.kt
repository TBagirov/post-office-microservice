package org.bagirov.subscriptionservice.dto

import org.bagirov.subscriptionservice.props.NotificationType

// Подтверждение подписки
data class SubscriptionConfirmedEvent(
    override val email: String,
    val username: String,
    val publicationName: String,
    val startDate: String,
    val duration: Int
) : NotificationEvent {
    override val type = NotificationType.SUBSCRIPTION_CONFIRMED
}