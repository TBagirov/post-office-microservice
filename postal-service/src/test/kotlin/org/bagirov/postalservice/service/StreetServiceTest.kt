package org.bagirov.postalservice.service

import io.mockk.*
import org.bagirov.postalservice.dto.request.StreetRequest
import org.bagirov.postalservice.dto.request.StreetUpdateRequest
import org.bagirov.postalservice.entity.RegionEntity
import org.bagirov.postalservice.entity.StreetEntity
import org.bagirov.postalservice.repository.DistrictRepository
import org.bagirov.postalservice.repository.RegionRepository
import org.bagirov.postalservice.repository.StreetRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class StreetServiceTest {

    private val streetRepository = mockk<StreetRepository>()
    private val regionRepository = mockk<RegionRepository>()
    private val districtRepository = mockk<DistrictRepository>()
    private val regionService = mockk<RegionService>()

    private lateinit var streetService: StreetService

    @BeforeEach
    fun setUp() {
        streetService = StreetService(streetRepository, regionRepository, districtRepository, regionService)
    }

    @Test
    fun `should get street by ID`() {
        val streetId = UUID.randomUUID()
        val street = StreetEntity(id = streetId, name = "Main Street")
        every { streetRepository.findById(streetId) } returns Optional.of(street)

        val result = streetService.getById(streetId)

        assertNotNull(result)
        assertEquals("Main Street", result.name)
        verify { streetRepository.findById(streetId) }
    }

    @Test
    fun `should save a new street`() {
        val region = RegionEntity(id = UUID.randomUUID(), name = "Test Region", streets = mutableSetOf())
        every { regionRepository.findAll() } returns listOf(region)

        val streetSlot = slot<StreetEntity>()
        every { streetRepository.save(capture(streetSlot)) } answers { streetSlot.captured.copy(id = UUID.randomUUID(), region = region) }

        val request = StreetRequest("New Street")
        val response = streetService.save(request)

        assertNotNull(response.id)
        assertEquals("New Street", response.name)
        assertEquals("Test Region", response.regionName)

        verify { streetRepository.save(any()) }
    }

    @Test
    fun `should update a street`() {
        val regionId = UUID.randomUUID()
        val existingStreet = StreetEntity(id = UUID.randomUUID(), name = "Old Street", region = null)
        val newRegion = RegionEntity(id = regionId, name = "Updated Region", streets = mutableSetOf())

        every { streetRepository.findById(existingStreet.id!!) } returns Optional.of(existingStreet)
        every { regionRepository.findById(regionId) } returns Optional.of(newRegion)

        val streetSlot = slot<StreetEntity>()
        every { streetRepository.save(capture(streetSlot)) } answers { streetSlot.captured }

        val updateRequest = StreetUpdateRequest(id = existingStreet.id!!, name = "Updated Street", regionId = regionId)
        val response = streetService.update(updateRequest)

        assertEquals("Updated Street", response.name)
        assertEquals("Updated Region", response.regionName)

        verify { streetRepository.save(any()) }
    }

    @Test
    fun `should delete a street`() {
        val streetId = UUID.randomUUID()
        val street = StreetEntity(id = streetId, name = "Street to Delete")
        every { streetRepository.findById(streetId) } returns Optional.of(street)
        every { streetRepository.delete(street) } just Runs

        val result = streetService.delete(streetId)

        assertNotNull(result)
        assertEquals("Street to Delete", result.name)
        verify { streetRepository.delete(street) }
    }
}


