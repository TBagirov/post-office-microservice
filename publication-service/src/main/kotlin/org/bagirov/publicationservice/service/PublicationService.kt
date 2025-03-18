package org.bagirov.publicationservice.service


import mu.KotlinLogging
import org.bagirov.publicationservice.dto.request.PublicationRequest
import org.bagirov.publicationservice.dto.request.update.PublicationUpdateRequest
import org.bagirov.publicationservice.dto.response.PublicationResponse
import org.bagirov.publicationservice.entity.PublicationEntity
import org.bagirov.publicationservice.entity.PublicationTypeEntity
import org.bagirov.publicationservice.repository.PublicationRepository
import org.bagirov.publicationservice.repository.PublicationTypeRepository
import org.bagirov.publicationservice.utill.convertToResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class PublicationService(
    private val publicationRepository: PublicationRepository,
    private val publicationTypeRepository: PublicationTypeRepository,
    private val minioService: MinioService
) {

    private val log = KotlinLogging.logger {}

    fun getById(id: UUID): PublicationResponse {
        log.info { "Fetching Publication by ID: $id" }
        return publicationRepository.findById(id)
            .orElseThrow { NoSuchElementException("Publication with ID ${id} not found") }
            .convertToResponseDto()
    }

    fun getAll(): List<PublicationResponse> {
        log.info { "Fetching all Publications" }
        return publicationRepository.findAll().map { it.convertToResponseDto() }
    }

    @Transactional
    fun save(publication: PublicationRequest): PublicationResponse {
        log.info { "Saving new Publication: ${publication.title}" }

        val publicationType = publicationTypeRepository.findByName(publication.type)
            ?: publicationTypeRepository.save(PublicationTypeEntity(name = publication.type))

        val publicationNew = PublicationEntity(
            index = publication.index,
            title = publication.title,
            type = publicationType,
            author = publication.author,
            coverUrl = publication.coverUrl,
            description = publication.description,
            price = publication.price
        )

        val publicationSave = publicationRepository.save(publicationNew)

        publicationType.publications?.add(publicationSave)

        log.info { "Publication saved successfully with ID: ${publicationSave.id}" }
        return publicationSave.convertToResponseDto()
    }

    @Transactional
    fun update(publication: PublicationUpdateRequest): PublicationResponse {
        log.info { "Updating Publication with ID: ${publication.id}" }

        val existingPublication = publicationRepository.findById(publication.id)
            .orElseThrow { NoSuchElementException("Publication with ID ${publication.id} not found") }

        val tempPublicationType = publication.typeName?.let { typeName ->
            publicationTypeRepository.findByName(typeName)
                ?: throw NoSuchElementException("Publication type '$typeName' not found")
        }

        existingPublication.apply {
            publication.index?.let { index = it }
            publication.title?.let { title = it }
            publication.description?.let { description = it }
            publication.author?.let { author = it }
            publication.price?.let { price = it }
            tempPublicationType?.let { type = it }
        }

        val savedPublication = publicationRepository.save(existingPublication)

        tempPublicationType?.publications?.let {
            if (!it.contains(savedPublication)) {
                it.add(savedPublication)
            }
        }

        log.info { "Publication updated successfully: ${savedPublication.id}" }
        return savedPublication.convertToResponseDto()
    }

    fun uploadCover(publicationId: UUID, file: MultipartFile): String {
        log.info { "Uploading cover for Publication ID: $publicationId" }

        val publication = publicationRepository.findById(publicationId)
            .orElseThrow { NoSuchElementException("Publication not found") }

        publication.coverUrl?.let {
            log.info { "Deleting old cover before uploading a new one" }
            minioService.deleteFile(it)
        }

        val coverUrl = minioService.uploadFile(file)
        publication.coverUrl = coverUrl

        publicationRepository.save(publication)

        log.info { "Cover uploaded successfully for Publication ID: $publicationId" }
        return coverUrl
    }

    @Transactional
    fun delete(id: UUID): PublicationResponse {
        log.info { "Deleting Publication with ID: $id" }

        val existingPublication = publicationRepository.findById(id)
            .orElseThrow { NoSuchElementException("Publication with ID ${id} not found") }

        publicationRepository.delete(existingPublication)

        log.info { "Publication deleted successfully: $id" }
        return existingPublication.convertToResponseDto()
    }

}
