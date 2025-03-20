package org.bagirov.subscriptionservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import org.bagirov.subscriptionservice.dto.SubscriptionPaymentEvent
import org.bagirov.subscriptionservice.props.SubscriptionStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.*

class KafkaConsumerServiceTest {

    private lateinit var kafkaConsumerService: KafkaConsumerService
    private val subscriptionService: SubscriptionService = mockk(relaxed = true)
    private val objectMapper: ObjectMapper = mockk()

    @BeforeEach
    fun setUp() {
        kafkaConsumerService = KafkaConsumerService(subscriptionService, objectMapper)
    }

    @Test
    fun `should consume payment event and update subscription status`() {
        val message = """{
            "subscriptionId": "550e8400-e29b-41d4-a716-446655440000",
            "status": "ACTIVE"
        }""".trimIndent()

        val subscriptionId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val status = SubscriptionStatus.ACTIVE

        val expectedEvent = SubscriptionPaymentEvent(subscriptionId, status)
        every { objectMapper.readValue(message, SubscriptionPaymentEvent::class.java) } returns expectedEvent
        every { subscriptionService.updateSubscriptionStatus(subscriptionId, status) } just Runs

        kafkaConsumerService.handlePaymentEvent(message)

        verify { subscriptionService.updateSubscriptionStatus(subscriptionId, status) }
    }

    @Test
    fun `should log error when message is invalid`() {
        val invalidMessage = "invalid json"
        val exception = IOException("Invalid JSON")

        every { objectMapper.readValue(invalidMessage, SubscriptionPaymentEvent::class.java) } throws exception

        kafkaConsumerService.handlePaymentEvent(invalidMessage)

        verify { subscriptionService wasNot Called }
    }
}
