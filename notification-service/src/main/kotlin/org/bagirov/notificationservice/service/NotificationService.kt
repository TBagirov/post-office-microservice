package org.bagirov.notificationservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.notificationservice.dto.*
import org.bagirov.notificationservice.props.NotificationType
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val emailService: EmailService,
    private val objectMapper: ObjectMapper
) {
    private val log = KotlinLogging.logger {}

    @KafkaListener(topics = ["notification-events"], groupId = "notification-service-group")
    fun processNotification(message: String) {
        try {
            val jsonNode = objectMapper.readTree(message)
            val type = jsonNode.get("type").asText()

            val event: NotificationEvent = when (NotificationType.valueOf(type)) {
                NotificationType.SUBSCRIPTION_CONFIRMED -> objectMapper.treeToValue(jsonNode, SubscriptionConfirmedEvent::class.java)
                NotificationType.POSTMAN_ASSIGNED -> objectMapper.treeToValue(jsonNode, PostmanAssignedEvent::class.java)
                NotificationType.SUBSCRIPTION_EXPIRED -> objectMapper.treeToValue(jsonNode, SubscriptionExpiredEvent::class.java)
                NotificationType.SUBSCRIPTION_CANCELLED -> objectMapper.treeToValue(jsonNode, SubscriptionCancelledEvent::class.java)
            }

            log.info { "Received notification event: $event" }
            sendNotificationEmail(event)

        } catch (e: Exception) {
            log.error(e) { "Ошибка обработки уведомления: ${e.message}" }
        }
    }

    private fun sendNotificationEmail(event: NotificationEvent) {
        when (event) {
            is SubscriptionConfirmedEvent -> emailService.sendEmail(
                event.email,
                "Подтверждение подписки",
                "subscription-confirmed.html",  // Название Thymeleaf шаблона
                mapOf(
                    "username" to event.username,
                    "publicationName" to event.publicationName,
                    "startDate" to event.startDate,
                    "duration" to event.duration
                )
            )
            is PostmanAssignedEvent -> emailService.sendEmail(
                event.email,
                "Назначен новый район",
                "postman-assigned.html",
                mapOf(
                    "username" to event.username,
                    "districtName" to event.districtName
                )
            )
            is SubscriptionExpiredEvent -> emailService.sendEmail(
                event.email,
                "Подписка истекла",
                "subscription-expired.html",
                mapOf(
                    "publicationName" to event.publicationName,
                    "expirationDate" to event.expirationDate
                )
            )
            is SubscriptionCancelledEvent -> emailService.sendEmail(
                event.email,
                "Подписка отменена",
                "subscription-cancelled.html",
                mapOf(
                    "publicationName" to event.publicationName,
                    "cancellationReason" to event.cancellationReason
                )
            )
        }
    }
}
