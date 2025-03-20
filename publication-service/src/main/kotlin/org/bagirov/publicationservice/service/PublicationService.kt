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
import org.bagirov.publicationservice.utill.convertToEventDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class PublicationService(
    private val publicationRepository: PublicationRepository,
    private val publicationTypeRepository: PublicationTypeRepository,
    private val minioService: MinioService,
    private val kafkaProducerService: KafkaProducerService
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
    fun save(request: PublicationRequest): PublicationResponse {
        log.info { "Saving new Publication: ${request.title}" }

        val publicationType = publicationTypeRepository.findByName(request.type)
            ?: publicationTypeRepository.save(PublicationTypeEntity(name = request.type))

        val publicationNew = PublicationEntity(
            index = request.index,
            title = request.title,
            type = publicationType,
            author = request.author,
            description = request.description,
            price = request.price
        )

        val publicationSave = publicationRepository.save(publicationNew)
        publicationType.publications?.add(publicationSave)

        log.info { "Publication saved successfully with ID: ${publicationSave.id}" }

        kafkaProducerService.sendPublicationCreatedEvent(publicationSave.convertToEventDto())
        log.info { "Sent Kafka event for publication created: ${request.title} with index ${request.index}" }

        return publicationSave.convertToResponseDto()
    }

    @Transactional
    fun update(request: PublicationUpdateRequest): PublicationResponse {
        log.info { "Updating Publication with ID: ${request.id}" }

        val existingPublication = publicationRepository.findById(request.id)
            .orElseThrow { NoSuchElementException("Publication with ID ${request.id} not found") }

        val tempPublicationType = request.typeName?.let { typeName ->
            publicationTypeRepository.findByName(typeName)
                ?: throw NoSuchElementException("Publication type '$typeName' not found")
        }

        existingPublication.apply {
            request.index?.let { index = it }
            request.title?.let { title = it }
            request.description?.let { description = it }
            request.author?.let { author = it }
            request.price?.let { price = it }
            tempPublicationType?.let { type = it }
        }

        val savedPublication = publicationRepository.save(existingPublication)

        tempPublicationType?.publications?.let {
            if (!it.contains(savedPublication)) {
                it.add(savedPublication)
            }
        }
        log.info { "Publication updated successfully: ${savedPublication.id}" }

        log.info { "Sent Kafka event for publication updated by id: ${request.id}" }
        kafkaProducerService.sendPublicationUpdatedEvent(request.convertToEventDto())

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
            .orElseThrow {
                log.error { "Publication with ID $id not found" }
                NoSuchElementException("Publication with ID ${id} not found")
            }

        publicationRepository.delete(existingPublication)
        log.info { "Publication deleted successfully: $id" }

        kafkaProducerService.sendPublicationDeletedEvent(id)
        return existingPublication.convertToResponseDto()
    }

}
