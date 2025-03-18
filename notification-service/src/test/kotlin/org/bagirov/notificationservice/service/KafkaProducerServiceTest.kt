package org.bagirov.notificationservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bagirov.notificationservice.dto.SubscriptionCancelledEvent
import org.bagirov.notificationservice.dto.SubscriptionConfirmedEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.kafka.core.KafkaTemplate
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
    fun `should send notification event successfully`() {
        // Arrange
        val event = SubscriptionConfirmedEvent(
            email = "test@example.com",
            username = "John Doe",
            publicationName = "Tech Magazine",
            startDate = "2024-04-01",
            duration = 6
        )
        val message = """{"email":"test@example.com","username":"John Doe","publicationName":"Tech Magazine","startDate":"2024-04-01","duration":6,"type":"SUBSCRIPTION_CONFIRMED"}"""
        val future = CompletableFuture.completedFuture(mockk<org.springframework.kafka.support.SendResult<String, String>>())

        every { objectMapper.writeValueAsString(event) } returns message
        every { kafkaTemplate.send("notification-events", message) } returns future

        // Act
        kafkaProducerService.sendNotification(event)

        // Assert
        verify { kafkaTemplate.send("notification-events", message) }
    }

    @Test
    fun `should log error when notification event sending fails`() {
        // Arrange
        val event = SubscriptionCancelledEvent(
            email = "test@example.com",
            publicationName = "Tech Weekly",
            cancellationReason = "User request"
        )
        val message = """{"email":"test@example.com","publicationName":"Tech Weekly","cancellationReason":"User request","type":"SUBSCRIPTION_CANCELLED"}"""
        val exception = RuntimeException("Kafka send failed")

        every { objectMapper.writeValueAsString(event) } returns message
        every { kafkaTemplate.send("notification-events", message) } throws exception

        // Act & Assert
        try {
            kafkaProducerService.sendNotification(event)
        } catch (e: Exception) {
            assert(e.message == "Kafka send failed")
        }
    }
}
