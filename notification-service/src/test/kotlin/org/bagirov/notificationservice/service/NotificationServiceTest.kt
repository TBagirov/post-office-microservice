package org.bagirov.notificationservice.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import org.bagirov.notificationservice.dto.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NotificationServiceTest {

    private lateinit var notificationService: NotificationService
    private val emailService: EmailService = mockk(relaxed = true)
    private val objectMapper: ObjectMapper = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        notificationService = NotificationService(emailService, objectMapper)
    }

    @Test
    fun `should process SUBSCRIPTION_CONFIRMED event successfully`() {
        // Arrange
        val event = SubscriptionConfirmedEvent(
            email = "test@example.com",
            username = "John Doe",
            publicationName = "Tech Magazine",
            startDate = "2024-04-01",
            duration = 6
        )
        val message = """{"email": "test@example.com", "username": "John Doe", "publicationName": "Tech Magazine", "startDate": "2024-04-01", "duration": 6, "type": "SUBSCRIPTION_CONFIRMED"}"""

        val jsonNode: JsonNode = mockk(relaxed = true)
        every { objectMapper.readTree(any<String>()) } returns jsonNode
        every { jsonNode.get("type").asText() } returns "SUBSCRIPTION_CONFIRMED"
        every { objectMapper.treeToValue(jsonNode, SubscriptionConfirmedEvent::class.java) } returns event

        // Act
        notificationService.processNotification(message)

        // Assert
        verify { emailService.sendEmail(any(), any(), any(), any()) }
    }

    @Test
    fun `should process POSTMAN_ASSIGNED event successfully`() {
        // Arrange
        val event = PostmanAssignedEvent(
            email = "postman@example.com",
            username = "Postman John",
            districtName = "District 5"
        )
        val message = """{"email": "postman@example.com", "username": "Postman John", "districtName": "District 5", "type": "POSTMAN_ASSIGNED"}"""

        val jsonNode: JsonNode = mockk(relaxed = true)
        every { objectMapper.readTree(any<String>()) } returns jsonNode
        every { jsonNode.get("type").asText() } returns "POSTMAN_ASSIGNED"
        every { objectMapper.treeToValue(jsonNode, PostmanAssignedEvent::class.java) } returns event

        // Act
        notificationService.processNotification(message)

        // Assert
        verify { emailService.sendEmail(any(), any(), any(), any()) }
    }

    @Test
    fun `should process SUBSCRIPTION_EXPIRED event successfully`() {
        // Arrange
        val event = SubscriptionExpiredEvent(
            email = "test@example.com",
            publicationName = "Tech Weekly",
            expirationDate = "2024-06-01"
        )
        val message = """{"email": "test@example.com", "publicationName": "Tech Weekly", "expirationDate": "2024-06-01", "type": "SUBSCRIPTION_EXPIRED"}"""

        val jsonNode: JsonNode = mockk(relaxed = true)
        every { objectMapper.readTree(any<String>()) } returns jsonNode
        every { jsonNode.get("type").asText() } returns "SUBSCRIPTION_EXPIRED"
        every { objectMapper.treeToValue(jsonNode, SubscriptionExpiredEvent::class.java) } returns event

        // Act
        notificationService.processNotification(message)

        // Assert
        verify { emailService.sendEmail(any(), any(), any(), any()) }
    }

    @Test
    fun `should process SUBSCRIPTION_CANCELLED event successfully`() {
        // Arrange
        val event = SubscriptionCancelledEvent(
            email = "test@example.com",
            publicationName = "Tech Journal",
            cancellationReason = "User request"
        )
        val message = """{"email": "test@example.com", "publicationName": "Tech Journal", "cancellationReason": "User request", "type": "SUBSCRIPTION_CANCELLED"}"""

        val jsonNode: JsonNode = mockk(relaxed = true)
        every { objectMapper.readTree(any<String>()) } returns jsonNode
        every { jsonNode.get("type").asText() } returns "SUBSCRIPTION_CANCELLED"
        every { objectMapper.treeToValue(jsonNode, SubscriptionCancelledEvent::class.java) } returns event

        // Act
        notificationService.processNotification(message)

        // Assert
        verify { emailService.sendEmail(any(), any(), any(), any()) }
    }

    @Test
    fun `should log error when processing invalid event`() {
        // Arrange
        val invalidMessage = """{"email": "test@example.com", "unknownField": "test", "type": "UNKNOWN_TYPE"}"""

        val jsonNode: JsonNode = mockk(relaxed = true)
        every { objectMapper.readTree(any<String>()) } returns jsonNode
        every { jsonNode.get("type").asText() } returns "UNKNOWN_TYPE"
        every { objectMapper.treeToValue(jsonNode, any<Class<NotificationEvent>>()) } throws IllegalArgumentException("Invalid event type")

        // Act
        notificationService.processNotification(invalidMessage)

        // Assert
        verify { emailService wasNot Called }
    }
}
