package org.bagirov.postalservice.service


import mu.KotlinLogging
import org.bagirov.postalservice.dto.response.PostmanResponse
import org.bagirov.postalservice.entity.PostmanEntity
import org.bagirov.postalservice.repository.PostmanRepository
import org.bagirov.postalservice.utill.convertToResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.NoSuchElementException

@Service
class PostmanService(
    private val postmanRepository: PostmanRepository
) {

    private val log = KotlinLogging.logger {}

    fun getById(id: UUID): PostmanResponse {
        log.info { "Fetching postman by ID: $id" }
        return postmanRepository.findById(id)
            .orElseThrow {
                log.error { "Postman with ID $id not found" }
                NoSuchElementException("Postman with ID $id not found")
            }
            .convertToResponseDto()
    }

    fun getAll(): List<PostmanResponse> {
        log.info { "Fetching all postmen" }
        return postmanRepository.findAll().map { it.convertToResponseDto() }
    }

    @Transactional
    fun update(postman: PostmanEntity): PostmanResponse {
        log.info { "Updating postman with ID: ${postman.id}" }

        // Найти существующего почтальона
        val existingPostman = postmanRepository.findById(postman.id!!)
            .orElseThrow {
                log.error { "Postman with ID ${postman.id} not found" }
                NoSuchElementException("Postman with ID ${postman.id} not found")
            }

        postmanRepository.save(existingPostman)

        log.info { "Postman updated successfully: ${existingPostman.id}" }
        return existingPostman.convertToResponseDto()
    }

}
