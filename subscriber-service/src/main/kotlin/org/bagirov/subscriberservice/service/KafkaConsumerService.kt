package org.bagirov.subscriberservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging
import org.bagirov.subscriberservice.dto.UserBecomeSubscriberEventDto
import org.bagirov.subscriberservice.entity.SubscriberEntity
import org.bagirov.subscriberservice.repository.SubscriberRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneOffset
import java.util.*

@Service
class KafkaConsumerService (
    private val subscriberRepository: SubscriberRepository,
    private val objectMapper: ObjectMapper
){

    private val log = KotlinLogging.logger {}

    @KafkaListener(topics = ["user-became-subscriber-events"], groupId = "subscriber-service-group")
    fun consumeUserBecameSubscriberEvent(message: String) {
        val event = objectMapper.readValue(message, UserBecomeSubscriberEventDto::class.java)

        val subscriber = SubscriberEntity (
            userId = event.userId,
            streetId = event.streetId,
            districtId = event.districtId,
            building = event.building,
            subAddress = event.subAddress,
            createdAt = Instant.ofEpochMilli(event.createdAt).atZone(ZoneOffset.UTC).toLocalDateTime()
        )

        subscriberRepository.save(subscriber)
        log.info("Создан подписчик: ${event.userId}")
    }


    @KafkaListener(topics = ["user-deleted-events"], groupId = "subscriber-service-group")
    fun consumeUserDeletedEvent(message: String) {
        try {
            val event = objectMapper.readValue<Map<String, String>>(message)
            val userId = UUID.fromString(event["id"] ?: throw IllegalArgumentException("userId is missing"))

            subscriberRepository.findByUserId(userId)?.let { subscriber ->
                subscriberRepository.delete(subscriber)
                log.info("Удален почтальон, связанный с пользователем $userId")
            } ?: log.warn("Почтальон с userId $userId не найден")

        } catch (e: Exception) {
            log.error(e) {"Ошибка обработки Kafka-сообщения о удалении пользователя: ${e.message}"}
        }
    }

}