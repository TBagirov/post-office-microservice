package org.bagirov.postalservice.dto

import org.bagirov.postalservice.props.NotificationType


interface NotificationEvent {
    val email: String
    val type: NotificationType
}