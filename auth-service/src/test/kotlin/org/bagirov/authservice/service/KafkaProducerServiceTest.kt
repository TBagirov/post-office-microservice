package org.bagirov.authservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.bagirov.authservice.dto.PostmanUpdatedEventDto
import org.bagirov.authservice.dto.UserBecomeSubscriberEventDto
import org.bagirov.authservice.dto.UserEventDto
import org.bagirov.authservice.props.Role
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.kafka.core.KafkaTemplate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class KafkaProducerServiceTest {

    @Mock
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Mock
    private lateinit var objectMapper: ObjectMapper

    @InjectMocks
    private lateinit var kafkaProducerService: KafkaProducerService

    @Test
    fun `sendUserCreatedEvent should send message to Kafka`() {
        val userEvent = UserEventDto(UUID.randomUUID(), LocalDateTime.now(), Role.GUEST)
        val message = "mocked-json"

        `when`(objectMapper.writeValueAsString(userEvent)).thenReturn(message)

        kafkaProducerService.sendUserCreatedEvent(userEvent)

        verify(kafkaTemplate).send("user-created-events", message)
    }

    @Test
    fun `sendUserDeletedEvent should send message to Kafka`() {
        val userId = UUID.randomUUID()
        val message = "{\"id\": \"$userId\"}"

        `when`(objectMapper.writeValueAsString(mapOf("id" to userId.toString()))).thenReturn(message)

        kafkaProducerService.sendUserDeletedEvent(userId)

        verify(kafkaTemplate).send("user-deleted-events", message)
    }

    @Test
    fun `sendPostmanUpdatedEvent should send message to Kafka`() {
        val event = PostmanUpdatedEventDto(UUID.randomUUID(), System.currentTimeMillis())
        val message = "mocked-json"

        `when`(objectMapper.writeValueAsString(event)).thenReturn(message)

        kafkaProducerService.sendPostmanUpdatedEvent(event)

        verify(kafkaTemplate).send("postman-updated-events", message)
    }

    @Test
    fun `sendUserBecameSubscriberEvent should send message to Kafka`() {
        val event = UserBecomeSubscriberEventDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "12A", "3B", System.currentTimeMillis())
        val message = "mocked-json"

        `when`(objectMapper.writeValueAsString(event)).thenReturn(message)

        kafkaProducerService.sendUserBecameSubscriberEvent(event)

        verify(kafkaTemplate).send("user-became-subscriber-events", message)
    }
}
