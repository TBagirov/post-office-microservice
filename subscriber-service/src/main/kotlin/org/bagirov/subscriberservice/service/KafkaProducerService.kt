package org.bagirov.subscriberservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.subscriberservice.dto.SubscriberEventDto
import org.bagirov.subscriberservice.dto.SubscriberUpdateEventDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    private val log = KotlinLogging.logger {}

    fun sendPublicationCreatedEvent(subscriberReportEvent: SubscriberEventDto) {
        val message = objectMapper.writeValueAsString(subscriberReportEvent)
        kafkaTemplate.send("subscriber-created-events", message)
        log.info { "Sent subscriber created event to Kafka: $message" }
    }

    fun sendSubscriberDeletedEvent(subscriberId: UUID) {
        val message = mapOf("id" to subscriberId.toString())
        kafkaTemplate.send("subscriber-deleted-events", objectMapper.writeValueAsString(message))
        log.info { "Sent subscriber deleted event to Kafka: $message" }
    }

    fun sendSubscriberUpdatedEvent(event: SubscriberUpdateEventDto) {
        val message = objectMapper.writeValueAsString(event)
        kafkaTemplate.send("subscriber-updated-events", message)
        log.info { "Sent subscriber updated event to Kafka: $message" }
    }

}