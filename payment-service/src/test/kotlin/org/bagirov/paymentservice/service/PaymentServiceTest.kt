package org.bagirov.paymentservice.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bagirov.paymentservice.client.PublicationServiceClient
import org.bagirov.paymentservice.dto.SubscriptionCreatedEvent
import org.bagirov.paymentservice.dto.response.client.PublicationResponseClient
import org.bagirov.paymentservice.props.PaymentStatus
import org.bagirov.paymentservice.props.SubscriptionStatus
import org.bagirov.paymentservice.repository.PaymentRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class PaymentServiceTest {

    private lateinit var paymentService: PaymentService
    private val paymentRepository: PaymentRepository = mockk(relaxed = true)
    private val paymentEventProducer: KafkaProducerService = mockk(relaxed = true)
    private val publicationServiceClient: PublicationServiceClient = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        paymentService = PaymentService(paymentRepository, paymentEventProducer, publicationServiceClient)
    }

    @Test
    fun `should process successful payment`() {
        // Arrange
        val subscriptionId = UUID.randomUUID()
        val subscriberId = UUID.randomUUID()
        val publicationId = UUID.randomUUID()
        val duration = 6
        val price = BigDecimal(100)
        val event = SubscriptionCreatedEvent(subscriptionId, subscriberId, publicationId, duration)
        val publicationResponse = PublicationResponseClient(publicationId, price)
        val expectedAmount = price.multiply(BigDecimal(duration))

        every { publicationServiceClient.getPublication(publicationId) } returns publicationResponse
        every { paymentRepository.save(any()) } answers { firstArg() }

        // Act
        paymentService.processPayment(event)

        // Assert
        verify { paymentRepository.save(withArg { assert(it.amount == expectedAmount) }) }
        verify { paymentEventProducer.sendPaymentEvent(withArg { assert(it.status == SubscriptionStatus.ACTIVE) }) }
    }

    @Test
    fun `should process failed payment`() {
        // Arrange
        val subscriptionId = UUID.randomUUID()
        val subscriberId = UUID.randomUUID()
        val publicationId = UUID.randomUUID()
        val duration = 6
        val price = BigDecimal(2000) // Высокая сумма, чтобы платеж не прошел
        val event = SubscriptionCreatedEvent(subscriptionId, subscriberId, publicationId, duration)
        val publicationResponse = PublicationResponseClient(publicationId, price)

        every { publicationServiceClient.getPublication(publicationId) } returns publicationResponse
        every { paymentRepository.save(any()) } answers { firstArg() }

        // Act
        paymentService.processPayment(event)

        // Assert
        verify { paymentRepository.save(withArg { assert(it.status == PaymentStatus.FAILED) }) }
        verify { paymentEventProducer.sendPaymentEvent(withArg { assert(it.status == SubscriptionStatus.CANCELLED) }) }
    }

    @Test
    fun `should handle exception during payment processing`() {
        // Arrange
        val subscriptionId = UUID.randomUUID()
        val subscriberId = UUID.randomUUID()
        val publicationId = UUID.randomUUID()
        val duration = 6
        val event = SubscriptionCreatedEvent(subscriptionId, subscriberId, publicationId, duration)

        every { publicationServiceClient.getPublication(publicationId) } throws RuntimeException("Service unavailable")

        // Act
        paymentService.processPayment(event)

        // Assert
        verify { paymentEventProducer.sendPaymentEvent(withArg { assert(it.status == SubscriptionStatus.CANCELLED) }) }
    }
}
