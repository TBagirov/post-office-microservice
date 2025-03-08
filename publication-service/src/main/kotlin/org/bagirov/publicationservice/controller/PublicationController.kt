package org.bagirov.publicationservice.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.bagirov.publicationservice.dto.request.PublicationRequest
import org.bagirov.publicationservice.dto.request.update.PublicationUpdateRequest
import org.bagirov.publicationservice.dto.response.PublicationResponse
import org.bagirov.publicationservice.dto.response.client.PublicationResponseClient
import org.bagirov.publicationservice.service.PublicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@CrossOrigin(origins = arrayOf("http://localhost:3000"))
@RestController
@RequestMapping("/api/publication")
@Tag(name = "PublicationController", description = "Контроллер для взаимодействия с изданиями")
class PublicationController(
    val publicationService: PublicationService
) {
    private val log = KotlinLogging.logger {}

    @GetMapping("/{id}")
    @Operation(
        summary = "Получение издания по id",
        description = "Получение данных издания по id"
    )
    fun getPublication(@PathVariable id: UUID): ResponseEntity<PublicationResponse> {
        log.info {"Request get Publication by id: $id"}
        return ResponseEntity.ok(publicationService.getById(id))
    }

    @GetMapping()
    @Operation(
        summary = "Получение всех изданий",
        description = "Получение данных о всех изданиях"
    )
    fun getAll():ResponseEntity<List<PublicationResponse>> {
        log.info {"Request get all Publication"}
        return ResponseEntity.ok(publicationService.getAll())
    }

    @Value("\${internal.api-secret}")
    private lateinit var apiSecret: String

    @GetMapping("/user/{id}")
    fun getPublicationById(
        @RequestHeader(value = "X-Internal-Call", required = false) secret: String?,
        @PathVariable(name = "id") userId: UUID
    ): ResponseEntity<PublicationResponseClient> {
        if (secret != apiSecret) {
            log.warn { "Forbidden access to /user/$userId. Invalid secret: $secret" }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        log.info { "Request Subscriber by id: $userId" }

        return ResponseEntity.ok(PublicationResponseClient(publicationService.getById(userId).id))
    }

    @PostMapping("/upload-cover/{id}")
    @Operation(summary = "Загрузка обложки", description = "Загружает изображение обложки в MinIO")
    fun uploadCover(
        @PathVariable id: UUID,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        val coverUrl = publicationService.uploadCover(id, file)
        return ResponseEntity.ok(coverUrl)
    }

    @PostMapping()
    @Operation(
        summary = "Добавление издания",
        description = "Добавление данных об издании"
    )
    fun save(@RequestBody publication: PublicationRequest): ResponseEntity<PublicationResponse> {
        log.info {"Request save Publication"}
        return ResponseEntity.ok(publicationService.save(publication))
    }

    @PutMapping()
    @Operation(
        summary = "Редактирование издания по id",
        description = "Редактирование данных издания по id"
    )
    fun update(@RequestBody publication: PublicationUpdateRequest): ResponseEntity<PublicationResponse> {
        log.info {"Request update Publication by id: ${publication.id}"}
        return ResponseEntity.ok(publicationService.update(publication))
    }



    @DeleteMapping()
    @Operation(
        summary = "Удаление издания по id",
        description = "Удаление издания по id, " +
                "удаленное издание в записях других таблиц изменится на null"
    )
    fun delete(@RequestParam id: UUID): ResponseEntity<PublicationResponse> {
        log.info {"Request delete Publication by id $id"}
        return ResponseEntity.ok(publicationService.delete(id))
    }
}