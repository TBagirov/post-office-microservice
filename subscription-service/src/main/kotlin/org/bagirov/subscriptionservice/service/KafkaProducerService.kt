package org.bagirov.subscriptionservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.subscriptionservice.dto.NotificationEvent
import org.bagirov.subscriptionservice.dto.SubscriptionCreatedEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,  // Теперь отправляем JSON
    private val objectMapper: ObjectMapper
) {

    private val log = KotlinLogging.logger {}

//    fun sendSubscriptionCreatedEvent(event: SubscriptionCreatedEvent) {
//        val message = objectMapper.writeValueAsString(event)  // Преобразуем DTO в JSON
//        kafkaTemplate.send("subscription-created", message)
//        log.info { "Sent subscription-created event to Kafka: $message" }
//    }
//
//    fun sendNotificationEvent(event: NotificationEvent) {
//        val message = objectMapper.writeValueAsString(event)
//        kafkaTemplate.send("notification-events", message)
//        log.info { "Sent notification event to Kafka: $message" }
//    }

    fun sendSubscriptionCreatedEvent(event: SubscriptionCreatedEvent) {
        try {
            val message = objectMapper.writeValueAsString(event)
            kafkaTemplate.send("subscription-created", message).get() // Дожидаемся завершения
            log.info { "Sent subscription-created event to Kafka: $message" }
        } catch (e: Exception) {
            log.error(e) {"Ошибка при отправке Kafka-сообщения: ${e.message}"}

        }
    }

    fun sendNotificationEvent(event: NotificationEvent) {
        try {
            val message = objectMapper.writeValueAsString(event)
            kafkaTemplate.send("notification-events", message).get() // Дожидаемся завершения
            log.info { "Sent notification event to Kafka: $message" }
        } catch (e: Exception) {
            log.error(e) {"Ошибка при отправке Kafka-сообщения: ${e.message}"}
        }
    }

}
