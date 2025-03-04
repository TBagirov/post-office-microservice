package org.bagirov.postalservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging
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

    @KafkaListener(topics = ["user-events"], groupId = "postal-service-group")
    fun consumeUserCreatedEvent(message : String){
        try {
            val userEvent = objectMapper.readValue(message, UserEventDto::class.java)

            if (userEvent.role == Role.POSTMAN) {
                val postman = PostmanEntity(userId = userEvent.id)
                postmanRepository.save(postman)
                log.info("Created postman for user ${userEvent.id}")
            }
        } catch (e: Exception) {
            log.error("Ошибка обработки Kafka-сообщения: ${e.message}", e)
        }
    }

    @KafkaListener(topics = ["user-deleted-events"], groupId = "postal-service-group")
    fun consumeUserDeletedEvent(message: String) {
        try {
            val event = objectMapper.readValue<Map<String, String>>(message)
            val userId = UUID.fromString(event["id"] ?: throw IllegalArgumentException("userId is missing"))

            postmanRepository.findByUserId(userId)?.let { postman ->
                postmanRepository.delete(postman)
                log.info("Удален почтальон, связанный с пользователем $userId")
            } ?: log.warn("Почтальон с userId $userId не найден")

        } catch (e: Exception) {
            log.error("Ошибка обработки Kafka-сообщения о удалении пользователя: ${e.message}", e)
        }
    }


}