package org.bagirov.postalservice.service

import org.junit.jupiter.api.Assertions.*
import io.mockk.*
import org.bagirov.postalservice.client.AuthServiceClient
import org.bagirov.postalservice.dto.PostmanAssignedEvent
import org.bagirov.postalservice.dto.request.DistrictRequest
import org.bagirov.postalservice.dto.request.DistrictUpdateRequest
import org.bagirov.postalservice.dto.response.DistrictResponse
import org.bagirov.postalservice.dto.response.client.AuthUserResponseClient
import org.bagirov.postalservice.entity.DistrictEntity
import org.bagirov.postalservice.entity.PostmanEntity
import org.bagirov.postalservice.entity.RegionEntity
import org.bagirov.postalservice.repository.DistrictRepository
import org.bagirov.postalservice.repository.PostmanRepository
import org.bagirov.postalservice.repository.RegionRepository
import org.junit.jupiter.api.*
import java.util.*

class DistrictServiceTest {

    private lateinit var districtService: DistrictService
    private val districtRepository: DistrictRepository = mockk()
    private val regionRepository: RegionRepository = mockk()
    private val postmanRepository: PostmanRepository = mockk()
    private val authServiceClient: AuthServiceClient = mockk()
    private val kafkaProducerService: KafkaProducerService = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        districtService = DistrictService(
            districtRepository, regionRepository, postmanRepository, authServiceClient, kafkaProducerService
        )
    }

    @Test
    fun `should return DistrictResponse when district exists`() {
        val districtId = UUID.randomUUID()
        val region = RegionEntity(id = UUID.randomUUID(), name = "Region 1")
        val postman = PostmanEntity(id = UUID.randomUUID(), userId = UUID.randomUUID())
        val districtEntity = DistrictEntity(id = districtId, region = region, postman = postman)

        every { districtRepository.findById(districtId) } returns Optional.of(districtEntity)

        val result: DistrictResponse = districtService.getById(districtId)

        assertNotNull(result)
        assertEquals(districtId, result.id)
        assertEquals(region.name, result.regionName)
        assertEquals(postman.id, result.postmanId)

        verify { districtRepository.findById(districtId) }
    }

    @Test
    fun `should throw exception when district not found`() {
        val districtId = UUID.randomUUID()
        every { districtRepository.findById(districtId) } returns Optional.empty()

        val exception = assertThrows<NoSuchElementException> {
            districtService.getById(districtId)
        }

        assertEquals("District with ID $districtId not found", exception.message)
        verify { districtRepository.findById(districtId) }
    }

    @Test
    fun `should return all districts`() {
        val districts = listOf(
            DistrictEntity(id = UUID.randomUUID(), region = RegionEntity(name = "Region 1")),
            DistrictEntity(id = UUID.randomUUID(), region = RegionEntity(name = "Region 2"))
        )

        every { districtRepository.findAll() } returns districts

        val result: List<DistrictResponse> = districtService.getAll()

        assertEquals(2, result.size)
        verify { districtRepository.findAll() }
    }

    @Test
    fun `should save new district and send Kafka event`() {
        val postmanId = UUID.randomUUID()
        val regionId = UUID.randomUUID()
        val postman = PostmanEntity(id = postmanId, userId = UUID.randomUUID(), districts = mutableListOf())
        val region = RegionEntity(id = regionId, name = "Test Region", districts = mutableListOf())
        val districtEntity = DistrictEntity(id = UUID.randomUUID(), region = region, postman = postman)

        every { regionRepository.findById(regionId) } returns Optional.of(region)
        every { postmanRepository.findById(postmanId) } returns Optional.of(postman)
        every { districtRepository.save(any()) } returns districtEntity
        every { authServiceClient.getUserDetails(postman.userId) } returns AuthUserResponseClient(
            userId = postman.userId, email = "test@example.com", username = "testuser"
        )

        val request = DistrictRequest(postmanId = postmanId, regionId = regionId)
        val result: DistrictResponse = districtService.save(request)

        assertNotNull(result)
        assertEquals(region.name, result.regionName)
        assertEquals(postmanId, result.postmanId)

        verify { districtRepository.save(any()) }
        verify { kafkaProducerService.sendNotificationEvent(any<PostmanAssignedEvent>()) }
    }

    @Test
    fun `should update existing district`() {
        val districtId = UUID.randomUUID()
        val postmanId = UUID.randomUUID()
        val regionId = UUID.randomUUID()
        val postman = PostmanEntity(id = postmanId, userId = UUID.randomUUID())
        val region = RegionEntity(id = regionId, name = "Updated Region")
        val existingDistrict = DistrictEntity(id = districtId, region = region, postman = postman)

        every { districtRepository.findById(districtId) } returns Optional.of(existingDistrict)
        every { regionRepository.findById(regionId) } returns Optional.of(region)
        every { postmanRepository.findById(postmanId) } returns Optional.of(postman)
        every { districtRepository.save(existingDistrict) } returns existingDistrict

        val request = DistrictUpdateRequest(id = districtId, postmanId = postmanId, regionId = regionId)
        val result: DistrictResponse = districtService.update(request)

        assertNotNull(result)
        assertEquals(region.name, result.regionName)
        assertEquals(postmanId, result.postmanId)

        verify { districtRepository.save(existingDistrict) }
    }

    @Test
    fun `should throw exception when updating a non-existing district`() {
        val districtId = UUID.randomUUID()
        val request = DistrictUpdateRequest(id = districtId, postmanId = UUID.randomUUID(), regionId = UUID.randomUUID())

        every { districtRepository.findById(districtId) } returns Optional.empty()

        val exception = assertThrows<NoSuchElementException> {
            districtService.update(request)
        }

        assertEquals("District with ID $districtId not found", exception.message)
        verify { districtRepository.findById(districtId) }
    }

    @Test
    fun `should delete district and return response`() {
        val districtId = UUID.randomUUID()
        val district = DistrictEntity(id = districtId, region = RegionEntity(name = "Region"))

        every { districtRepository.findById(districtId) } returns Optional.of(district)
        every { districtRepository.deleteById(districtId) } just Runs

        val result: DistrictResponse = districtService.delete(districtId)

        assertNotNull(result)
        assertEquals(districtId, result.id)

        verify { districtRepository.deleteById(districtId) }
    }

    @Test
    fun `should throw exception when deleting non-existing district`() {
        val districtId = UUID.randomUUID()

        every { districtRepository.findById(districtId) } returns Optional.empty()

        val exception = assertThrows<NoSuchElementException> {
            districtService.delete(districtId)
        }

        assertEquals("District with ID $districtId not found", exception.message)
        verify { districtRepository.findById(districtId) }
    }
}
