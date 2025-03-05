package org.bagirov.subscriberservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.bagirov.subscriberservice.dto.UserBecomeSubscriberEventDto
import org.bagirov.subscriberservice.entity.SubscriberEntity
import org.bagirov.subscriberservice.repository.SubscriberRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneOffset

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

}