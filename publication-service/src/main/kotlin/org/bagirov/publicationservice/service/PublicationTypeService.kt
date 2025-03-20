package org.bagirov.publicationservice.service


import mu.KotlinLogging
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

    private val log = KotlinLogging.logger {}

    fun getById(id: UUID): PublicationTypeResponse {
        log.info { "Fetching PublicationType by ID: $id" }
        return publicationTypeRepository.findById(id)
            .orElseThrow { NoSuchElementException("PublicationType with ID ${id} not found") }
            .convertToResponseDto()
    }

    fun getAll(): List<PublicationTypeResponse> {
        log.info { "Fetching all PublicationTypes" }
        return publicationTypeRepository.findAll().map { it.convertToResponseDto() }
    }

    @Transactional
    fun save(publicationType: PublicationTypeRequest): PublicationTypeResponse {
        log.info { "Saving new PublicationType: ${publicationType.type}" }

        val savePublicationType = publicationTypeRepository.save(
            PublicationTypeEntity(
                name = publicationType.type
            )
        )

        log.info { "PublicationType saved with ID: ${savePublicationType.id}" }
        return savePublicationType.convertToResponseDto()
    }

    @Transactional
    fun update(publicationType: PublicationTypeEntity): PublicationTypeResponse {
        log.info { "Updating PublicationType with ID: ${publicationType.id}" }

        val existingPublicationType = publicationTypeRepository.findById(publicationType.id!!)
            .orElseThrow { NoSuchElementException("PublicationType with ID ${publicationType.id} not found") }

        existingPublicationType.name = publicationType.name

        publicationTypeRepository.save(existingPublicationType)

        log.info { "PublicationType updated successfully: ${existingPublicationType.id}" }
        return existingPublicationType.convertToResponseDto()
    }

    @Transactional
    fun delete(id: UUID): PublicationTypeResponse {
        log.info { "Deleting PublicationType with ID: $id" }

        val existingPublicationType = publicationTypeRepository.findById(id)
            .orElseThrow { NoSuchElementException("PublicationType with ID ${id} not found") }

        existingPublicationType.publications = null

        publicationTypeRepository.delete(existingPublicationType)

        log.info { "PublicationType deleted: $id" }
        return existingPublicationType.convertToResponseDto()
    }

}
