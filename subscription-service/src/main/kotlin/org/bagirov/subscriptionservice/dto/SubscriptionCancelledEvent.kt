package org.bagirov.subscriptionservice.dto

import org.bagirov.subscriptionservice.props.NotificationType

data class SubscriptionCancelledEvent(
    override val email: String,
    val publicationName: String,
    val cancellationReason: String
) : NotificationEvent {
    override val type = NotificationType.SUBSCRIPTION_CANCELLED
}