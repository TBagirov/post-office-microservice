package org.bagirov.notificationservice.service


import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.notificationservice.dto.NotificationEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val log = KotlinLogging.logger {}

    fun sendNotification(event: NotificationEvent) {
        try {
            val messagePayload = objectMapper.writeValueAsString(event)

            val message: Message<String> = MessageBuilder
                .withPayload(messagePayload)
                .setHeader("type", event.type.name)
                .build()

            kafkaTemplate.send("notification-events", messagePayload)

            log.info { "Notification event sent: $messagePayload" }
        } catch (e: Exception) {
            log.error(e) { "Ошибка отправки уведомления: ${e.message}" }
        }
    }
}

