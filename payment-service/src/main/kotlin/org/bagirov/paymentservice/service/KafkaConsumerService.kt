package org.bagirov.paymentservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.paymentservice.dto.SubscriptionCreatedEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaConsumerService(
    private val paymentService: PaymentService,
    private val objectMapper: ObjectMapper
) {

    private val log = KotlinLogging.logger {}

    @KafkaListener(topics = ["subscription-created"], groupId = "payment-service-group")
    fun handleSubscriptionEvent(message: String) {
        try {
            val event = objectMapper.readValue(message, SubscriptionCreatedEvent::class.java) // Десериализуем JSON в DTO
            paymentService.processPayment(event)
            log.info("Processed subscription-created event: $event")
        } catch (e: Exception) {
            log.error(e) { "Error processing subscription-created event: ${e.message}" }
        }
    }
}
