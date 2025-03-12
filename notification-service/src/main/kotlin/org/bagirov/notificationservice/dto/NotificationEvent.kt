package org.bagirov.notificationservice.dto

import org.bagirov.notificationservice.props.NotificationType


interface NotificationEvent {
    val email: String
    val type: NotificationType
}