package org.bagirov.postalservice.service

import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.util.*
import org.bagirov.postalservice.entity.RegionEntity
import org.bagirov.postalservice.repository.RegionRepository

class RegionServiceTest {

    private val regionRepository = mockk<RegionRepository>()
    private val districtService = mockk<DistrictService>(relaxed = true)

    private lateinit var regionService: RegionService

    @BeforeEach
    fun setUp() {
        regionService = RegionService(regionRepository, districtService)
    }

    @Test
    fun `should return region by ID`() {
        val regionId = UUID.randomUUID()
        val region = RegionEntity(id = regionId, name = "Test Region")

        every { regionRepository.findById(regionId) } returns Optional.of(region)

        val response = regionService.getById(regionId)

        assertNotNull(response)
        assertEquals("Test Region", response.name)
        assertEquals(regionId, response.id)

        verify { regionRepository.findById(regionId) }
    }

    @Test
    fun `should throw exception when region not found`() {
        val regionId = UUID.randomUUID()

        every { regionRepository.findById(regionId) } returns Optional.empty()

        val exception = assertThrows<NoSuchElementException> {
            regionService.getById(regionId)
        }

        assertEquals("Region with ID $regionId not found", exception.message)
        verify { regionRepository.findById(regionId) }
    }

    @Test
    fun `should return all regions`() {
        val region1 = RegionEntity(id = UUID.randomUUID(), name = "Region1")
        val region2 = RegionEntity(id = UUID.randomUUID(), name = "Region2")

        every { regionRepository.findAll() } returns listOf(region1, region2)

        val regions = regionService.getAll()

        assertEquals(2, regions.size)
        assertEquals("Region1", regions[0].name)
        assertEquals("Region2", regions[1].name)

        verify { regionRepository.findAll() }
    }

    @Test
    fun `should save a new region`() {
        val region = RegionEntity(id = UUID.randomUUID(), name = "New Region")
        val regionSlot = slot<RegionEntity>()

        every { regionRepository.save(capture(regionSlot)) } answers { regionSlot.captured }
        every { districtService.saveOnlyRegion(any()) } just Runs

        val response = regionService.save(region)

        assertEquals("New Region", response.name)
        verify { regionRepository.save(any()) }
        verify { districtService.saveOnlyRegion(any()) }
    }

    @Test
    fun `should update an existing region`() {
        val regionId = UUID.randomUUID()
        val existingRegion = RegionEntity(id = regionId, name = "Old Region")
        val updatedRegion = RegionEntity(id = regionId, name = "Updated Region")

        every { regionRepository.findById(regionId) } returns Optional.of(existingRegion)
        every { regionRepository.save(any()) } answers { updatedRegion }

        val response = regionService.update(updatedRegion)

        assertEquals("Updated Region", response.name)
        verify { regionRepository.save(any()) }
    }

    @Test
    fun `should throw exception when updating region without ID`() {
        val region = RegionEntity(name = "Region Without ID")

        val exception = assertThrows<IllegalArgumentException> {
            regionService.update(region)
        }

        assertEquals("Region id must not be null", exception.message)
    }

    @Test
    fun `should delete a region`() {
        val regionId = UUID.randomUUID()
        val existingRegion = RegionEntity(id = regionId, name = "Region to Delete")

        every { regionRepository.findById(regionId) } returns Optional.of(existingRegion)
        every { regionRepository.delete(existingRegion) } just Runs

        val response = regionService.delete(regionId)

        assertEquals(regionId, response.id)
        assertEquals("Region to Delete", response.name)

        verify { regionRepository.delete(existingRegion) }
    }

    @Test
    fun `should throw exception when deleting a non-existent region`() {
        val regionId = UUID.randomUUID()

        every { regionRepository.findById(regionId) } returns Optional.empty()

        val exception = assertThrows<NoSuchElementException> {
            regionService.delete(regionId)
        }

        assertEquals("Region with ID $regionId not found", exception.message)
    }
}