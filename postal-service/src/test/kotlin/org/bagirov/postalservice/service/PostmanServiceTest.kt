package org.bagirov.postalservice.service


import org.junit.jupiter.api.Assertions.*
import io.mockk.*
import org.bagirov.postalservice.dto.response.PostmanResponse
import org.bagirov.postalservice.entity.PostmanEntity
import org.bagirov.postalservice.repository.PostmanRepository
import org.junit.jupiter.api.*
import java.util.*

class PostmanServiceTest {

    private lateinit var postmanService: PostmanService
    private val postmanRepository: PostmanRepository = mockk()

    @BeforeEach
    fun setUp() {
        postmanService = PostmanService(postmanRepository)
    }

    @Test
    fun `should return PostmanResponse when postman exists`() {
        val postmanId = UUID.randomUUID()
        val postmanEntity = PostmanEntity(id = postmanId, userId = UUID.randomUUID())

        every { postmanRepository.findById(postmanId) } returns Optional.of(postmanEntity)

        val result: PostmanResponse = postmanService.getById(postmanId)

        assertNotNull(result)
        assertEquals(postmanId, result.id)
        verify { postmanRepository.findById(postmanId) }
    }

    @Test
    fun `should throw exception when postman not found`() {
        val postmanId = UUID.randomUUID()
        every { postmanRepository.findById(postmanId) } returns Optional.empty()

        val exception = assertThrows<NoSuchElementException> {
            postmanService.getById(postmanId)
        }

        assertEquals("Postman with ID $postmanId not found", exception.message)
        verify { postmanRepository.findById(postmanId) }
    }

    @Test
    fun `should return all postmen`() {
        val postmen = listOf(
            PostmanEntity(id = UUID.randomUUID(), userId = UUID.randomUUID()),
            PostmanEntity(id = UUID.randomUUID(), userId = UUID.randomUUID())
        )

        every { postmanRepository.findAll() } returns postmen

        val result: List<PostmanResponse> = postmanService.getAll()

        assertEquals(2, result.size)
        verify { postmanRepository.findAll() }
    }

    @Test
    fun `should update postman and return updated response`() {
        val postmanId = UUID.randomUUID()
        val existingPostman = PostmanEntity(id = postmanId, userId = UUID.randomUUID())

        every { postmanRepository.findById(postmanId) } returns Optional.of(existingPostman)
        every { postmanRepository.save(existingPostman) } returns existingPostman

        val result: PostmanResponse = postmanService.update(existingPostman)

        assertNotNull(result)
        assertEquals(postmanId, result.id)
        verify { postmanRepository.save(existingPostman) }
    }

    @Test
    fun `should throw exception when updating a non-existing postman`() {
        val postman = PostmanEntity(id = UUID.randomUUID(), userId = UUID.randomUUID())

        every { postmanRepository.findById(postman.id!!) } returns Optional.empty()

        val exception = assertThrows<NoSuchElementException> {
            postmanService.update(postman)
        }

        assertEquals("Postman with ID ${postman.id} not found", exception.message)
        verify { postmanRepository.findById(postman.id!!) }
    }
}