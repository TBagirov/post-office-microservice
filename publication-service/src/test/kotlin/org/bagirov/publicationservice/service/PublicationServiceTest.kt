package org.bagirov.publicationservice.service

import io.mockk.*
import org.bagirov.publicationservice.dto.request.PublicationRequest
import org.bagirov.publicationservice.dto.request.update.PublicationUpdateRequest
import org.bagirov.publicationservice.entity.PublicationEntity
import org.bagirov.publicationservice.entity.PublicationTypeEntity
import org.bagirov.publicationservice.repository.PublicationRepository
import org.bagirov.publicationservice.repository.PublicationTypeRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.util.*

class PublicationServiceTest {

    private val publicationRepository: PublicationRepository = mockk()
    private val publicationTypeRepository: PublicationTypeRepository = mockk()
    private val minioService: MinioService = mockk()

    private lateinit var publicationService: PublicationService

    @BeforeEach
    fun setUp() {
        publicationService = PublicationService(publicationRepository, publicationTypeRepository, minioService)
    }

    @Test
    fun `should get publication by ID`() {
        val publicationId = UUID.randomUUID()
        val publicationType = PublicationTypeEntity(name = "Magazine")
        val publication = PublicationEntity(
            id = publicationId,
            index = "12345",
            title = "Test Publication",
            author = "John Doe",
            description = "Sample description",
            coverUrl = null,
            price = BigDecimal(9.99),
            type = publicationType
        )

        every { publicationRepository.findById(publicationId) } returns Optional.of(publication)

        val result = publicationService.getById(publicationId)

        assertNotNull(result)
        assertEquals(publication.title, result.title)
        assertEquals(publication.index, result.index)
    }

    @Test
    fun `should throw exception when publication not found`() {
        val publicationId = UUID.randomUUID()
        every { publicationRepository.findById(publicationId) } returns Optional.empty()

        val exception = assertThrows<NoSuchElementException> { publicationService.getById(publicationId) }
        assertEquals("Publication with ID $publicationId not found", exception.message)
    }

    @Test
    fun `should save a new publication`() {
        val request = PublicationRequest(
            index = "12345",
            title = "Test Publication",
            author = "John Doe",
            description = "Sample description",
            coverUrl = null,
            type = "Magazine",
            price = BigDecimal(9.99)
        )

        val publicationType = PublicationTypeEntity(name = request.type)
        val publication = PublicationEntity(
            id = UUID.randomUUID(),
            index = request.index,
            title = request.title,
            author = request.author,
            description = request.description,
            coverUrl = request.coverUrl,
            price = request.price,
            type = publicationType
        )

        every { publicationTypeRepository.findByName(request.type) } returns null
        every { publicationTypeRepository.save(any()) } returns publicationType
        every { publicationRepository.save(any()) } returns publication

        val result = publicationService.save(request)

        assertNotNull(result)
        assertEquals(request.title, result.title)
        assertEquals(request.index, result.index)

        verify { publicationTypeRepository.save(any()) }
        verify { publicationRepository.save(any()) }
    }

    @Test
    fun `should update publication`() {
        val publicationId = UUID.randomUUID()
        val publicationType = PublicationTypeEntity(name = "Magazine")
        val existingPublication = PublicationEntity(
            id = publicationId,
            index = "12345",
            title = "Old Title",
            author = "John Doe",
            description = "Old description",
            coverUrl = null,
            price = BigDecimal(9.99),
            type = publicationType
        )

        val updateRequest = PublicationUpdateRequest(
            id = publicationId,
            index = "54321",
            title = "New Title",
            description = "New description",
            author = "Jane Doe",
            typeName = "New Type",
            coverUrl = "new-cover-url",
            price = BigDecimal(19.99)
        )

        every { publicationRepository.findById(publicationId) } returns Optional.of(existingPublication)
        every { publicationRepository.save(any()) } returns existingPublication
        every { publicationTypeRepository.findByName(updateRequest.typeName!!) } returns publicationType

        val result = publicationService.update(updateRequest)

        assertNotNull(result)
        assertEquals(updateRequest.title, result.title)
        assertEquals(updateRequest.description, result.description)
    }

    @Test
    fun `should delete publication`() {
        val publicationId = UUID.randomUUID()
        val publication = PublicationEntity(
            id = publicationId,
            index = "12345",
            title = "Test Publication",
            author = "John Doe",
            description = "Sample description",
            coverUrl = null,
            price = BigDecimal(9.99),
            type = PublicationTypeEntity(name = "Magazine")
        )

        every { publicationRepository.findById(publicationId) } returns Optional.of(publication)
        every { publicationRepository.delete(publication) } just Runs

        val result = publicationService.delete(publicationId)

        assertNotNull(result)
        verify { publicationRepository.delete(publication) }
    }

    @Test
    fun `should upload cover`() {
        val publicationId = UUID.randomUUID()
        val file: MultipartFile = mockk()
        val publication = PublicationEntity(
            id = publicationId,
            index = "12345",
            title = "Test Publication",
            author = "John Doe",
            description = "Sample description",
            coverUrl = null,
            price = BigDecimal(9.99),
            type = PublicationTypeEntity(name = "Magazine")
        )

        every { publicationRepository.findById(publicationId) } returns Optional.of(publication)
        every { minioService.uploadFile(file) } returns "new-cover-url"
        every { publicationRepository.save(any()) } returns publication

        val coverUrl = publicationService.uploadCover(publicationId, file)

        assertEquals("new-cover-url", coverUrl)
        verify { minioService.uploadFile(file) }
        verify { publicationRepository.save(publication) }
    }
}
