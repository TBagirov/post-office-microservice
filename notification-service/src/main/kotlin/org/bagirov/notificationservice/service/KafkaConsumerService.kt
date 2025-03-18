package org.bagirov.notificationservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.notificationservice.dto.*
import org.bagirov.notificationservice.props.NotificationType
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaConsumerService(
    private val notificationService: NotificationService,
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
            notificationService.sendNotificationEmail(event)

        } catch (e: Exception) {
            log.error(e) { "Notification processing error: ${e.message}" }
        }
    }
}