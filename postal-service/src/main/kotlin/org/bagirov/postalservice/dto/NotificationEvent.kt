package org.bagirov.postalservice.dto

import org.bagirov.postalservice.props.NotificationType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Интерфейс для событий уведомлений (postal-service)")
interface NotificationEvent {
    @get:Schema(description = "Email получателя уведомления")
    val email: String

    @get:Schema(description = "Тип уведомления")
    val type: NotificationType
}