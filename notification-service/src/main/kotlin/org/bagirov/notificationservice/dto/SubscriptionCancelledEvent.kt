package org.bagirov.notificationservice.dto

import org.bagirov.notificationservice.props.NotificationType

data class SubscriptionCancelledEvent(
    override val email: String,
    val publicationName: String,
    val cancellationReason: String
) : NotificationEvent {
    override val type = NotificationType.SUBSCRIPTION_CANCELLED
}