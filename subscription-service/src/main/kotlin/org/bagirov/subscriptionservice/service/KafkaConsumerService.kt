package org.bagirov.subscriptionservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.subscriptionservice.dto.SubscriptionPaymentEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaConsumerService(
    private val subscriptionService: SubscriptionService,
    private val objectMapper: ObjectMapper
) {

    private val log = KotlinLogging.logger {}

    @KafkaListener(
        topics = ["payment-events"],
        groupId = "subscription-service-group"
    )
    fun handlePaymentEvent(message: String) {
        try {
            val event = objectMapper.readValue(message, SubscriptionPaymentEvent::class.java)
            subscriptionService.updateSubscriptionStatus(event.subscriptionId, event.status)
            log.info("Processed Payment Event: $event")
        } catch (e: Exception) {
            log.error(e) {"Ошибка обработки Kafka-сообщения: ${e.message}"}
        }
    }
}
