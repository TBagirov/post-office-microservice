package org.bagirov.subscriptionservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import org.bagirov.subscriptionservice.dto.NotificationEvent
import org.bagirov.subscriptionservice.dto.SubscriptionCreatedEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.kafka.core.KafkaTemplate
import java.util.*
import java.util.concurrent.CompletableFuture

class KafkaProducerServiceTest {

    private lateinit var kafkaProducerService: KafkaProducerService
    private val kafkaTemplate: KafkaTemplate<String, String> = mockk(relaxed = true)
    private val objectMapper: ObjectMapper = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        kafkaProducerService = KafkaProducerService(kafkaTemplate, objectMapper)
    }

    @Test
    fun `should send subscription created event successfully`() {
        // Arrange
        val event = SubscriptionCreatedEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 6)
        val message = """{"subscriptionId":"${event.subscriptionId}","subscriberId":"${event.subscriberId}","publicationId":"${event.publicationId}","duration":${event.duration}}"""
        val future = CompletableFuture.completedFuture(mockk<org.springframework.kafka.support.SendResult<String, String>>())

        every { objectMapper.writeValueAsString(event) } returns message
        every { kafkaTemplate.send("subscription-created", message) } returns future

        // Act
        kafkaProducerService.sendSubscriptionCreatedEvent(event)

        // Assert
        verify { kafkaTemplate.send("subscription-created", message) }
    }

    @Test
    fun `should log error when subscription created event sending fails`() {
        // Arrange
        val event = SubscriptionCreatedEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 6)
        val message = """{"subscriptionId":"${event.subscriptionId}","subscriberId":"${event.subscriberId}","publicationId":"${event.publicationId}","duration":${event.duration}}"""
        val exception = RuntimeException("Kafka send failed")

        every { objectMapper.writeValueAsString(event) } returns message
        every { kafkaTemplate.send("subscription-created", message) } throws exception

        // Act & Assert
        try {
            kafkaProducerService.sendSubscriptionCreatedEvent(event)
        } catch (e: Exception) {
            assert(e.message == "Kafka send failed")
        }
    }

    @Test
    fun `should send notification event successfully`() {
        // Arrange
        val event = mockk<NotificationEvent>(relaxed = true)
        every { event.email } returns "test@example.com"
        every { event.type.toString() } returns "SUBSCRIPTION_CONFIRMED"

        val message = """{"email":"test@example.com","type":"SUBSCRIPTION_CONFIRMED"}"""
        val future = CompletableFuture.completedFuture(mockk<org.springframework.kafka.support.SendResult<String, String>>())

        every { objectMapper.writeValueAsString(event) } returns message
        every { kafkaTemplate.send("notification-events", message) } returns future

        // Act
        kafkaProducerService.sendNotificationEvent(event)

        // Assert
        verify { kafkaTemplate.send("notification-events", message) }
    }

    @Test
    fun `should log error when notification event sending fails`() {
        // Arrange
        val event = mockk<NotificationEvent>(relaxed = true)
        every { event.email } returns "test@example.com"
        every { event.type.toString() } returns "SUBSCRIPTION_CONFIRMED"

        val message = """{"email":"test@example.com","type":"SUBSCRIPTION_CONFIRMED"}"""
        val exception = RuntimeException("Kafka send failed")

        every { objectMapper.writeValueAsString(event) } returns message
        every { kafkaTemplate.send("notification-events", message) } throws exception

        // Act & Assert
        try {
            kafkaProducerService.sendNotificationEvent(event)
        } catch (e: Exception) {
            assert(e.message == "Kafka send failed")
        }
    }
}
