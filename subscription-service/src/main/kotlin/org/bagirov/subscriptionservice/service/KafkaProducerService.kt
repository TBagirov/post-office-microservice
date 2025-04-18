package org.bagirov.subscriptionservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.subscriptionservice.dto.NotificationEvent
import org.bagirov.subscriptionservice.dto.SubscriptionCreatedEvent
import org.bagirov.subscriptionservice.dto.SubscriptionUpdatedEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    private val log = KotlinLogging.logger {}


    fun sendSubscriptionCreatedEvent(event: SubscriptionCreatedEvent) {
        try {
            val message = objectMapper.writeValueAsString(event)
            kafkaTemplate.send("subscription-created-events", message).get() // Дожидаемся завершения
            log.info { "Sent subscription-created event to Kafka: $message" }
        } catch (e: Exception) {
            log.error(e) {"Error when sending a Kafka message: ${e.message}"}

        }
    }

    fun sendSubscriptionUpdate(event: SubscriptionUpdatedEvent) {
        try {
            val message = objectMapper.writeValueAsString(event)
            kafkaTemplate.send("subscription-updated-events", message).get() // Дожидаемся завершения
            log.info { "Sent subscription-created event to Kafka: $message" }
        } catch (e: Exception) {
            log.error(e) {"Error when sending a Kafka message: ${e.message}"}

        }
    }

    fun sendNotificationEvent(event: NotificationEvent) {
        try {
            val message = objectMapper.writeValueAsString(event)
            kafkaTemplate.send("notification-events", message).get() // Дожидаемся завершения
            log.info { "Sent notification event to Kafka: $message" }
        } catch (e: Exception) {
            log.error(e) {"Error when sending a Kafka message: ${e.message}"}
        }
    }

    fun sendSubscriptionDeletedEvent(subscriptionId: UUID) {
        try {
            val message = mapOf("id" to subscriptionId.toString())
            kafkaTemplate.send("subscription-deleted-events", objectMapper.writeValueAsString(message))
            log.info { "Sent subscription deleted event to Kafka: $message" }
        } catch (e: Exception) {
            log.error(e) {"Error when sending a Kafka message: ${e.message}"}
        }
    }


}
