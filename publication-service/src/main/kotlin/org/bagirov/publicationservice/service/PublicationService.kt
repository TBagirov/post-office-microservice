package org.bagirov.publicationservice.service


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

    fun getById(id: UUID): PublicationResponse =
        publicationRepository.findById(id)
            .orElseThrow { NoSuchElementException("Publication with ID ${id} not found") }
            .convertToResponseDto()

    fun getAll(): List<PublicationResponse> =
        publicationRepository.findAll().map { it.convertToResponseDto() }

    @Transactional
    fun save(publication: PublicationRequest): PublicationResponse {

        // Если тип издания существует, используем его, иначе создаем новый.
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

        // Сохраняем новый publicationNew
        val publicationSave = publicationRepository.save(publicationNew)

        // Добавляем в коллекцию (она уже инициализирована)
        publicationType.publications?.add(publicationSave)

        return publicationSave.convertToResponseDto()
    }

    @Transactional
    fun update(publication: PublicationUpdateRequest): PublicationResponse {

        // Найти существующее издание или выбросить исключение
        val existingPublication = publicationRepository.findById(publication.id)
            .orElseThrow { NoSuchElementException("Publication with ID ${publication.id} not found") }

        // Найти тип публикации только если type не null
        val tempPublicationType = publication.typeName?.let { typeName ->
            publicationTypeRepository.findByName(typeName)
                ?: throw NoSuchElementException("Publication type '$typeName' not found")
        }


        // Обновление существующей публикации
        existingPublication.apply {
            publication.index?.let { index = it }
            publication.title?.let { title = it }
            publication.description?.let { description = it }
            publication.author?.let { author = it }
            publication.price?.let { price = it }
            tempPublicationType?.let { type = it }
        }

        // Сохранить изменения в публикации
        val savedPublication = publicationRepository.save(existingPublication)

        // Обновить связь публикации с типом (если её не было)
        tempPublicationType?.publications?.let {
            if (!it.contains(savedPublication)) {
                it.add(savedPublication)
            }
        }
        return savedPublication.convertToResponseDto()
    }

    fun uploadCover(publicationId: UUID, file: MultipartFile): String {
        val publication = publicationRepository.findById(publicationId)
            .orElseThrow { NoSuchElementException("Publication not found") }

        // Удаляем старую обложку, если есть
        publication.coverUrl?.let { minioService.deleteFile(it) }

        // Загружаем новую обложку
        val coverUrl = minioService.uploadFile(file)
        publication.coverUrl = coverUrl

        publicationRepository.save(publication)

        return coverUrl
    }

    @Transactional
    fun delete(id: UUID): PublicationResponse {

        // Найти существующее издание
        val existingPublication = publicationRepository.findById(id)
            .orElseThrow { NoSuchElementException("Publication with ID ${id} not found") }

        // Удалить издание
        publicationRepository.delete(existingPublication)

        return existingPublication.convertToResponseDto()
    }

}
