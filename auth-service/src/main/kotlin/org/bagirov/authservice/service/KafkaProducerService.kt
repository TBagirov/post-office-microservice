package org.bagirov.authservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.authservice.dto.UserEventDto
import org.bagirov.authservice.dto.PostmanUpdatedEventDto
import org.bagirov.authservice.dto.UserBecomeSubscriberEventDto
import org.bagirov.authservice.dto.UserUpdatedEventDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    private val log = KotlinLogging.logger {}

    fun sendUserCreatedEvent(userEvent: UserEventDto) {
        val message = objectMapper.writeValueAsString(userEvent)
        kafkaTemplate.send("user-created-events", message)
        log.info { "Sent user created event to Kafka: $message" }
    }

    fun sendUserDeletedEvent(userId: UUID) {
        val message = mapOf("id" to userId.toString())
        kafkaTemplate.send("user-deleted-events", objectMapper.writeValueAsString(message))
        log.info { "Sent user deleted event to Kafka: $message" }
    }

    fun sendPostmanUpdatedEvent(event: PostmanUpdatedEventDto) {
        val message = objectMapper.writeValueAsString(event)
        kafkaTemplate.send("postman-updated-events", message)
        log.info { "Sent postman updated event to Kafka: $message" }
    }

    fun sendUserUpdatedEvent(event: UserUpdatedEventDto) {
        val message = objectMapper.writeValueAsString(event)
        kafkaTemplate.send("user-updated-events", message)
        log.info { "Sent updated user event to Kafka: $message" }
    }

    fun sendUserBecameSubscriberEvent(event: UserBecomeSubscriberEventDto) {
        val message = objectMapper.writeValueAsString(event)
        kafkaTemplate.send("user-became-subscriber-events", message)
        log.info { "Sent user became subscriber event to Kafka: $message" }
    }

}