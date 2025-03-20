package org.bagirov.subscriptionservice.service

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.bagirov.subscriptionservice.client.AuthServiceClient
import org.bagirov.subscriptionservice.client.PublicationServiceClient
import org.bagirov.subscriptionservice.client.SubscriberServiceUserClient
import org.bagirov.subscriptionservice.config.CustomUserDetails
import org.bagirov.subscriptionservice.dto.SubscriptionCreatedEvent
import org.bagirov.subscriptionservice.dto.request.SubscriptionRequest
import org.bagirov.subscriptionservice.dto.response.client.PublicationResponseClient
import org.bagirov.subscriptionservice.dto.response.client.SubscriberResponseUserClient
import org.bagirov.subscriptionservice.entity.SubscriptionEntity
import org.bagirov.subscriptionservice.props.SubscriptionStatus
import org.bagirov.subscriptionservice.repository.SubscriptionRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SubscriptionServiceTest {

    private val subscriptionRepository: SubscriptionRepository = mockk()
    private val subscriberServiceUserClient: SubscriberServiceUserClient = mockk()
    private val publicationServiceClient: PublicationServiceClient = mockk()
    private val authServiceClient: AuthServiceClient = mockk()
    private val kafkaProducerService: KafkaProducerService = mockk()

    private lateinit var subscriptionService: SubscriptionService

    @BeforeEach
    fun setUp() {
        subscriptionService = SubscriptionService(
            subscriptionRepository,
            subscriberServiceUserClient,
            publicationServiceClient,
            authServiceClient,
            kafkaProducerService
        )
    }

    @Test
    fun `should get subscription by id`() {
        val subscriptionId = UUID.randomUUID()
        val subscription = SubscriptionEntity(
            id = subscriptionId,
            subscriberId = UUID.randomUUID(),
            publicationId = UUID.randomUUID(),
            duration = 12,
            status = SubscriptionStatus.ACTIVE
        )

        every { subscriptionRepository.findById(subscriptionId) } returns Optional.of(subscription)

        val result = subscriptionService.getById(subscriptionId)

        assertNotNull(result)
        assertEquals(subscriptionId, result.id)
    }

    @Test
    fun `should throw exception when subscription not found`() {
        val subscriptionId = UUID.randomUUID()
        every { subscriptionRepository.findById(subscriptionId) } returns Optional.empty()

        assertThrows<NoSuchElementException> {
            subscriptionService.getById(subscriptionId)
        }
    }

    @Test
    fun `should save subscription and send Kafka event`() {
        val user = CustomUserDetails(UUID.randomUUID(), emptyList())
        val publicationId = UUID.randomUUID()
        val request = SubscriptionRequest(publicationId, 6)
        val subscriberResponse = SubscriberResponseUserClient(UUID.randomUUID())
        val publicationResponse = PublicationResponseClient(publicationId, BigDecimal.TEN, "Test Publication")
        val subscriptionEntity = SubscriptionEntity(
            id = UUID.randomUUID(),
            subscriberId = subscriberResponse.subscriberId,
            publicationId = publicationId,
            duration = request.duration,
            status = SubscriptionStatus.PENDING_PAYMENT
        )

        every { subscriberServiceUserClient.getSubscriberByUserId(user.getUserId()) } returns subscriberResponse
        every { publicationServiceClient.getPublication(publicationId) } returns publicationResponse
        every { subscriptionRepository.save(any()) } returns subscriptionEntity
        justRun { kafkaProducerService.sendSubscriptionCreatedEvent(any()) }

        val result = subscriptionService.save(user, request)

        assertNotNull(result)
        assertEquals(SubscriptionStatus.PENDING_PAYMENT, result.status)
        verify { kafkaProducerService.sendSubscriptionCreatedEvent(any<SubscriptionCreatedEvent>()) }
    }
}