package org.bagirov.postalservice.service


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

    fun getById(id: UUID): PostmanResponse = postmanRepository.findById(id)
        .orElseThrow { NoSuchElementException("Postman with ID ${id} not found") }
        .convertToResponseDto()

    fun getAll(): List<PostmanResponse> = postmanRepository.findAll().map { it.convertToResponseDto() }

    @Transactional
    fun update(postman: PostmanEntity): PostmanResponse {
        // Найти существующего почтальона
        val existingPostman = postmanRepository.findById(postman.id!!)
            .orElseThrow { NoSuchElementException("Postman with ID ${postman.id} not found") }

        postmanRepository.save(existingPostman)

        return existingPostman.convertToResponseDto()
    }



}
