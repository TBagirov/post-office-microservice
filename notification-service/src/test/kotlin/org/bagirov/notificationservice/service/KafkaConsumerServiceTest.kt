package org.bagirov.notificationservice.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bagirov.notificationservice.dto.SubscriptionCancelledEvent
import org.bagirov.notificationservice.dto.SubscriptionConfirmedEvent
import org.bagirov.notificationservice.props.NotificationType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException

class KafkaConsumerServiceTest {

    private lateinit var kafkaConsumerService: KafkaConsumerService
    private val emailService: EmailService = mockk(relaxed = true)
    private val objectMapper: ObjectMapper = mockk()

    @BeforeEach
    fun setUp() {
        kafkaConsumerService = KafkaConsumerService(emailService, objectMapper)
    }

    @Test
    fun `should process SubscriptionConfirmedEvent`() {
        val message = """{
            "email": "test@example.com",
            "username": "John Doe",
            "publicationName": "Tech Magazine",
            "startDate": "2024-04-01",
            "duration": 6,
            "type": "SUBSCRIPTION_CONFIRMED"
        }""".trimIndent()

        val jsonNode: JsonNode = mockk()
        val event = SubscriptionConfirmedEvent("test@example.com", "John Doe", "Tech Magazine", "2024-04-01", 6)

        every { objectMapper.readTree(message) } returns jsonNode
        every { jsonNode.get("type").asText() } returns NotificationType.SUBSCRIPTION_CONFIRMED.name
        every { objectMapper.treeToValue(jsonNode, SubscriptionConfirmedEvent::class.java) } returns event

        kafkaConsumerService.processNotification(message)

        verify {
            emailService.sendEmail(
                event.email,
                "Подтверждение подписки",
                "subscription-confirmed.html",
                mapOf(
                    "username" to event.username,
                    "publicationName" to event.publicationName,
                    "startDate" to event.startDate,
                    "duration" to event.duration
                )
            )
        }
    }

    @Test
    fun `should log error when message is invalid`() {
        val invalidMessage = "invalid json"
        val exception = IOException("Invalid JSON")

        every { objectMapper.readTree(invalidMessage) } throws exception

        kafkaConsumerService.processNotification(invalidMessage)

        verify { emailService wasNot Called }
    }

    @Test
    fun `should process SubscriptionCancelledEvent`() {
        val message = """{
            "email": "test@example.com",
            "publicationName": "Tech Weekly",
            "cancellationReason": "User request",
            "type": "SUBSCRIPTION_CANCELLED"
        }""".trimIndent()

        val jsonNode: JsonNode = mockk()
        val event = SubscriptionCancelledEvent("test@example.com", "Tech Weekly", "User request")

        every { objectMapper.readTree(message) } returns jsonNode
        every { jsonNode.get("type").asText() } returns NotificationType.SUBSCRIPTION_CANCELLED.name
        every { objectMapper.treeToValue(jsonNode, SubscriptionCancelledEvent::class.java) } returns event

        kafkaConsumerService.processNotification(message)

        verify {
            emailService.sendEmail(
                event.email,
                "Подписка отменена",
                "subscription-cancelled.html",
                mapOf(
                    "publicationName" to event.publicationName,
                    "cancellationReason" to event.cancellationReason
                )
            )
        }
    }
}