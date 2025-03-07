package org.bagirov.publicationservice.service


import org.bagirov.publicationservice.dto.request.PublicationTypeRequest
import org.bagirov.publicationservice.dto.response.PublicationTypeResponse
import org.bagirov.publicationservice.entity.PublicationTypeEntity
import org.bagirov.publicationservice.repository.PublicationTypeRepository
import org.bagirov.publicationservice.utill.convertToResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PublicationTypeService(
    private val publicationTypeRepository: PublicationTypeRepository
) {

    fun getById(id: UUID): PublicationTypeResponse = publicationTypeRepository.findById(id)
        .orElseThrow { NoSuchElementException("PublicationType with ID ${id} not found") }
        .convertToResponseDto()

    fun getAll(): List<PublicationTypeResponse> = publicationTypeRepository.findAll().map { it.convertToResponseDto() }


    @Transactional
    fun save(publicationType: PublicationTypeRequest): PublicationTypeResponse {

        val savePublicationType = publicationTypeRepository.save(
            PublicationTypeEntity(
                name = publicationType.type
            )
        )

        return savePublicationType.convertToResponseDto()
    }

    @Transactional
    fun update(publicationType: PublicationTypeEntity): PublicationTypeResponse {

        // Найти существующий тип издания
        val existingPublicationType = publicationTypeRepository.findById(publicationType.id!!)
            .orElseThrow { NoSuchElementException("Publication Type with ID ${publicationType.id} not found") }

        existingPublicationType.name = publicationType.name

        publicationTypeRepository.save(existingPublicationType)

        return existingPublicationType.convertToResponseDto()
    }

    @Transactional
    fun delete(id: UUID): PublicationTypeResponse {

        // Найти существующий тип издания
        val existingPublicationType = publicationTypeRepository.findById(id)
            .orElseThrow { NoSuchElementException("Publication Type with ID ${id} not found") }

        existingPublicationType.publications = null

        // Удалить тип издания
        publicationTypeRepository.delete(existingPublicationType)

        return existingPublicationType.convertToResponseDto()
    }

}
