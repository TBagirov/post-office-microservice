package org.bagirov.notificationservice.dto

import org.bagirov.notificationservice.props.NotificationType


// Назначение почтальона на район
data class PostmanAssignedEvent(
    override val email: String,
    val username: String,
    val districtName: String
) : NotificationEvent {
    override val type = NotificationType.POSTMAN_ASSIGNED
}