package org.bagirov.postalservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging
import org.bagirov.postalservice.dto.PostmanUpdatedEventDto
import org.bagirov.postalservice.dto.UserEventDto
import org.bagirov.postalservice.entity.PostmanEntity
import org.bagirov.postalservice.props.Role
import org.bagirov.postalservice.repository.PostmanRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.util.*

@Service
class KafkaConsumerService (
    private val postmanRepository: PostmanRepository,
    private val objectMapper: ObjectMapper
){

    private val log = KotlinLogging.logger {}

    @KafkaListener(topics = ["user-created-events"], groupId = "postal-service-group")
    fun consumeUserCreatedEvent(message: String) {
        try {
            val userEvent = objectMapper.readValue(message, UserEventDto::class.java)

            if (userEvent.role == Role.POSTMAN) {
                val postman = PostmanEntity(userId = userEvent.id)
                postmanRepository.save(postman)
                log.info { "Postman created for user ID: ${userEvent.id}" }
            }
        } catch (e: Exception) {
            log.error(e) { "Error processing Kafka message: ${e.message}" }
        }
    }

    @KafkaListener(topics = ["user-deleted-events"], groupId = "postal-service-group")
    fun consumeUserDeletedEvent(message: String) {
        try {
            val event = objectMapper.readValue<Map<String, String>>(message)
            val userId = UUID.fromString(event["id"] ?: throw IllegalArgumentException("userId is missing"))

            postmanRepository.findByUserId(userId)?.let { postman ->
                postmanRepository.delete(postman)
                log.info { "Postman deleted for user ID: $userId" }
            } ?: log.warn { "Postman with userId $userId not found" }

        } catch (e: Exception) {
            log.error(e) { "Error processing Kafka message for user deletion: ${e.message}" }
        }
    }

    @KafkaListener(topics = ["postman-updated-events"], groupId = "postal-service-group")
    fun consumeUserUpdatedEvent(message: String) {
        try {
            val event = objectMapper.readValue(message, PostmanUpdatedEventDto::class.java)

            postmanRepository.findByUserId(event.userId)?.let { postman ->
                postmanRepository.save(postman)
                log.info { "Postman updated for user ID: ${event.userId}" }
            } ?: log.warn { "Postman with userId ${event.userId} not found" }

        } catch (e: Exception) {
            log.error(e) { "Error processing Kafka message for postman update: ${e.message}" }
        }
    }

}