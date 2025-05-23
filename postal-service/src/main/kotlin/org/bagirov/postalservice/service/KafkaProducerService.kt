package org.bagirov.postalservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.postalservice.dto.NotificationEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,  // Теперь отправляем JSON
    private val objectMapper: ObjectMapper
) {

    private val log = KotlinLogging.logger {}

    fun sendNotificationEvent(event: NotificationEvent) {
        try {
            val message = objectMapper.writeValueAsString(event)
            kafkaTemplate.send("notification-events", message)
            log.info { "Successfully sent notification event to Kafka: $message" }
        } catch (e: Exception) {
            log.error(e) { "Failed to send notification event: ${e.message}" }
        }
    }

}