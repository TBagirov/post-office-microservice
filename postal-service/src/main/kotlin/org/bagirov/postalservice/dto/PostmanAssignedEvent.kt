package org.bagirov.postalservice.dto

import org.bagirov.postalservice.props.NotificationType

data class PostmanAssignedEvent(
    override val email: String,  // Почта почтальона
    val username: String,     // логин почтальона
    val districtName: String     // Название нового региона
) : NotificationEvent {
    override val type = NotificationType.POSTMAN_ASSIGNED
}