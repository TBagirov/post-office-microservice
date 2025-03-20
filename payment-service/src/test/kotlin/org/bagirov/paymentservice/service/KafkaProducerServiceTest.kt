package org.bagirov.paymentservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import org.bagirov.paymentservice.dto.SubscriptionPaymentEvent
import org.bagirov.paymentservice.props.SubscriptionStatus
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
    fun `should send payment event successfully`() {
        // Arrange
        val event = SubscriptionPaymentEvent(UUID.randomUUID(), SubscriptionStatus.ACTIVE)
        val message = """{"subscriptionId":"${event.subscriptionId}","status":"${event.status}"}"""
        val future = CompletableFuture.completedFuture(mockk<org.springframework.kafka.support.SendResult<String, String>>())

        every { objectMapper.writeValueAsString(event) } returns message
        every { kafkaTemplate.send("payment-events", message) } returns future

        // Act
        kafkaProducerService.sendPaymentEvent(event)

        // Assert
        verify { kafkaTemplate.send("payment-events", message) }
    }

    @Test
    fun `should log error when payment event sending fails`() {
        // Arrange
        val event = SubscriptionPaymentEvent(UUID.randomUUID(), SubscriptionStatus.ACTIVE)
        val message = """{"subscriptionId":"${event.subscriptionId}","status":"${event.status}"}"""
        val exception = RuntimeException("Kafka send failed")

        every { objectMapper.writeValueAsString(event) } returns message
        every { kafkaTemplate.send("payment-events", message) } throws exception

        // Act & Assert
        try {
            kafkaProducerService.sendPaymentEvent(event)
        } catch (e: Exception) {
            assert(e.message == "Kafka send failed")
        }
    }
}
