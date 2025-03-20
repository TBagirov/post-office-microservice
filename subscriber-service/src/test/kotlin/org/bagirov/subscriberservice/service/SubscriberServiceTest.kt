package org.bagirov.subscriberservice.service

import org.junit.jupiter.api.Assertions.*

import io.mockk.*
import org.bagirov.subscriberservice.client.PostalServiceClient
import org.bagirov.subscriberservice.config.CustomUserDetails
import org.bagirov.subscriberservice.dto.request.SubscriberUpdateRequest
import org.bagirov.subscriberservice.dto.response.SubscriberResponse
import org.bagirov.subscriberservice.dto.response.client.StreetDistrictResponse
import org.bagirov.subscriberservice.entity.SubscriberEntity
import org.bagirov.subscriberservice.repository.SubscriberRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertNotNull

class SubscriberServiceTest {

    private val subscriberRepository: SubscriberRepository = mockk()
    private val postalServiceClient: PostalServiceClient = mockk()
    private lateinit var subscriberService: SubscriberService

    @BeforeEach
    fun setUp() {
        subscriberService = SubscriberService(subscriberRepository, postalServiceClient)
    }

    @Test
    fun `should get subscriber by ID`() {
        val subscriberId = UUID.randomUUID()
        val subscriber = SubscriberEntity(
            id = subscriberId,
            userId = UUID.randomUUID(),
            streetId = UUID.randomUUID(),
            districtId = UUID.randomUUID(),
            building = "10",
            subAddress = "Apt 2",
            createdAt = java.time.LocalDateTime.now()
        )

        every { subscriberRepository.findById(subscriberId) } returns Optional.of(subscriber)

        val result: SubscriberResponse = subscriberService.getById(subscriberId)

        assertNotNull(result)
        assertEquals(subscriberId, result.id)
        assertEquals("10", result.building)
    }

    @Test
    fun `should throw exception when subscriber not found`() {
        val subscriberId = UUID.randomUUID()
        every { subscriberRepository.findById(subscriberId) } returns Optional.empty()

        assertThrows<NoSuchElementException> { subscriberService.getById(subscriberId) }
    }

    @Test
    fun `should update subscriber`() {
        val userId = UUID.randomUUID()
        val subscriber = SubscriberEntity(
            id = UUID.randomUUID(),
            userId = userId,
            streetId = UUID.randomUUID(),
            districtId = UUID.randomUUID(),
            building = "10",
            subAddress = "Apt 2",
            createdAt = java.time.LocalDateTime.now()
        )

        val updateRequest = SubscriberUpdateRequest(
            building = "20",
            subAddress = "Apt 5",
            streetName = "Main St"
        )

        val newStreetDistrict = StreetDistrictResponse(
            streetId = UUID.randomUUID(),
            districtId = UUID.randomUUID()
        )

        val userDetails = CustomUserDetails(userId, listOf())

        every { subscriberRepository.findByUserId(userId) } returns subscriber
        every { postalServiceClient.getStreetAndDistrict("Main St") } returns newStreetDistrict
        every { subscriberRepository.save(any()) } returns subscriber

        val result = subscriberService.update(userDetails, updateRequest)

        assertNotNull(result)
        assertEquals("20", result.building)
        assertEquals("Apt 5", result.subAddress)
    }
}
