package org.bagirov.notificationservice.dto

import org.bagirov.notificationservice.props.NotificationType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Событие о назначении почтальону нового района (notification-service)")
data class PostmanAssignedEvent(
    @Schema(description = "Email почтальона")
    override val email: String,

    @Schema(description = "Username почтальона")
    val username: String,

    @Schema(description = "Название района (региона)")
    val districtName: String
) : NotificationEvent {
    @Schema(description = "Тип уведомления: POSTMAN_ASSIGNED")
    override val type = NotificationType.POSTMAN_ASSIGNED
}