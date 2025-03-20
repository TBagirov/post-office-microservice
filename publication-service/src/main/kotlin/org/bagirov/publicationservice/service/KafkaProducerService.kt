package org.bagirov.publicationservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.publicationservice.dto.PublicationReportEventDto
import org.bagirov.publicationservice.dto.request.update.PublicationUpdateEventDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    private val log = KotlinLogging.logger {}

    fun sendPublicationCreatedEvent(publicationReportEvent: PublicationReportEventDto) {
        val message = objectMapper.writeValueAsString(publicationReportEvent)
        kafkaTemplate.send("publication-created-events", message)
        log.info { "Sent publication created event to Kafka: $message" }
    }

    fun sendPublicationDeletedEvent(publicationId: UUID) {
        val message = mapOf("id" to publicationId.toString())
        kafkaTemplate.send("publication-deleted-events", objectMapper.writeValueAsString(message))
        log.info { "Sent publication deleted event to Kafka: $message" }
    }

    fun sendPublicationUpdatedEvent(event: PublicationUpdateEventDto) {
        val message = objectMapper.writeValueAsString(event)
        kafkaTemplate.send("publication-updated-events", message)
        log.info { "Sent publication updated event to Kafka: $message" }
    }

}