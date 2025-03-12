package org.bagirov.subscriptionservice.dto

import org.bagirov.subscriptionservice.props.NotificationType


interface NotificationEvent {
    val email: String
    val type: NotificationType
}