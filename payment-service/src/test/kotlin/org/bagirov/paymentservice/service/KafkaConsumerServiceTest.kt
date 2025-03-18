package org.bagirov.paymentservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import org.bagirov.paymentservice.dto.SubscriptionCreatedEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.*

class KafkaConsumerServiceTest {

    private lateinit var kafkaConsumerService: KafkaConsumerService
    private val paymentService: PaymentService = mockk(relaxed = true)
    private val objectMapper: ObjectMapper = mockk()

    @BeforeEach
    fun setUp() {
        kafkaConsumerService = KafkaConsumerService(paymentService, objectMapper)
    }

    @Test
    fun `should consume subscription-created event and process payment`() {
        // Arrange
        val event = SubscriptionCreatedEvent(
            subscriptionId = UUID.randomUUID(),
            subscriberId = UUID.randomUUID(),
            publicationId = UUID.randomUUID(),
            duration = 6
        )

        val message = """{
            "subscriptionId": "${event.subscriptionId}",
            "subscriberId": "${event.subscriberId}",
            "publicationId": "${event.publicationId}",
            "duration": ${event.duration}
        }""".trimIndent()

        every { objectMapper.readValue(message, SubscriptionCreatedEvent::class.java) } returns event
        every { paymentService.processPayment(event) } just Runs

        // Act
        kafkaConsumerService.handleSubscriptionEvent(message)

        // Assert
        verify { paymentService.processPayment(event) }
    }

    @Test
    fun `should log error when message is invalid`() {
        // Arrange
        val invalidMessage = "invalid json"
        val exception = IOException("Invalid JSON")

        every { objectMapper.readValue(invalidMessage, SubscriptionCreatedEvent::class.java) } throws exception

        // Act
        kafkaConsumerService.handleSubscriptionEvent(invalidMessage)

        // Assert
        verify { paymentService wasNot Called }
    }
}
