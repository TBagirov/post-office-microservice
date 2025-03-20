package org.bagirov.publicationservice.service

import io.mockk.*
import org.bagirov.publicationservice.dto.request.PublicationTypeRequest
import org.bagirov.publicationservice.entity.PublicationTypeEntity
import org.bagirov.publicationservice.repository.PublicationTypeRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class PublicationTypeServiceTest {

    private val publicationTypeRepository: PublicationTypeRepository = mockk()
    private lateinit var publicationTypeService: PublicationTypeService

    @BeforeEach
    fun setUp() {
        publicationTypeService = PublicationTypeService(publicationTypeRepository)
    }

    @Test
    fun `should get publication type by ID`() {
        val typeId = UUID.randomUUID()
        val publicationType = PublicationTypeEntity(id = typeId, name = "Magazine")

        every { publicationTypeRepository.findById(typeId) } returns Optional.of(publicationType)

        val result = publicationTypeService.getById(typeId)

        assertNotNull(result)
        assertEquals(publicationType.name, result.type)
    }

    @Test
    fun `should throw exception when publication type not found`() {
        val typeId = UUID.randomUUID()
        every { publicationTypeRepository.findById(typeId) } returns Optional.empty()

        val exception = assertThrows<NoSuchElementException> { publicationTypeService.getById(typeId) }
        assertEquals("PublicationType with ID $typeId not found", exception.message)
    }

    @Test
    fun `should get all publication types`() {
        val publicationTypes = listOf(
            PublicationTypeEntity(id = UUID.randomUUID(), name = "Magazine"),
            PublicationTypeEntity(id = UUID.randomUUID(), name = "Book")
        )

        every { publicationTypeRepository.findAll() } returns publicationTypes

        val result = publicationTypeService.getAll()

        assertNotNull(result)
        assertEquals(2, result.size)
    }

    @Test
    fun `should save a new publication type`() {
        val request = PublicationTypeRequest(type = "NewType")
        val publicationType = PublicationTypeEntity(id = UUID.randomUUID(), name = request.type)

        every { publicationTypeRepository.save(any()) } returns publicationType

        val result = publicationTypeService.save(request)

        assertNotNull(result)
        assertEquals(request.type, result.type)
        verify { publicationTypeRepository.save(any()) }
    }

    @Test
    fun `should update publication type`() {
        val typeId = UUID.randomUUID()
        val existingType = PublicationTypeEntity(id = typeId, name = "OldType")
        val updatedType = PublicationTypeEntity(id = typeId, name = "UpdatedType")

        every { publicationTypeRepository.findById(typeId) } returns Optional.of(existingType)
        every { publicationTypeRepository.save(any()) } returns updatedType

        val result = publicationTypeService.update(updatedType)

        assertNotNull(result)
        assertEquals(updatedType.name, result.type)
        verify { publicationTypeRepository.save(any()) }
    }

    @Test
    fun `should delete publication type`() {
        val typeId = UUID.randomUUID()
        val publicationType = PublicationTypeEntity(id = typeId, name = "ToDelete")

        every { publicationTypeRepository.findById(typeId) } returns Optional.of(publicationType)
        every { publicationTypeRepository.delete(publicationType) } just Runs

        val result = publicationTypeService.delete(typeId)

        assertNotNull(result)
        verify { publicationTypeRepository.delete(publicationType) }
    }
}
