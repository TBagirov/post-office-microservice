package org.bagirov.authservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import mu.KotlinLogging
import org.bagirov.authservice.dto.UserEventResponse
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    private val log = KotlinLogging.logger {}

    fun sendUserCreatedEvent(userEvent: UserEventResponse) {
        val objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule()) // Поддержка LocalDateTime
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        val message = objectMapper.writeValueAsString(userEvent)
        kafkaTemplate.send("user-events", message)
        log.info { "Sent user created event to Kafka: $message" }

    }

}