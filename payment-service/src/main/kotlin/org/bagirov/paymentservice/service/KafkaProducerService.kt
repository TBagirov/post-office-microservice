package org.bagirov.paymentservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.paymentservice.dto.SubscriptionPaymentEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    private val log = KotlinLogging.logger {}

    fun sendPaymentEvent(event: SubscriptionPaymentEvent) {
        val message = objectMapper.writeValueAsString(event)
        kafkaTemplate.send("payment-events", message)
        log.info { "Sent payment event to Kafka: $message" }
    }
}
