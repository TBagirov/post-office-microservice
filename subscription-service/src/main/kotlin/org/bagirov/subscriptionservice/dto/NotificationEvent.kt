package org.bagirov.subscriptionservice.dto

import org.bagirov.subscriptionservice.props.NotificationType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Базовый интерфейс для событий уведомлений")
interface NotificationEvent {
    @get:Schema(description = "Email получателя уведомления")
    val email: String

    @get:Schema(description = "Тип уведомления")
    val type: NotificationType
}