package org.bagirov.publicationservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.bagirov.publicationservice.dto.request.PublicationTypeRequest
import org.bagirov.publicationservice.dto.response.PublicationTypeResponse
import org.bagirov.publicationservice.entity.PublicationTypeEntity
import org.bagirov.publicationservice.service.PublicationTypeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@CrossOrigin(origins = arrayOf("http://localhost:3000"))
@RestController
@RequestMapping("/api/publication/type")
@Tag(name = "PublicationTypeController", description = "Контроллер для взаимодействия с типом изданий")
class PublicationTypeController(
    val publicationTypeService: PublicationTypeService
) {
    private val log = KotlinLogging.logger {}

    @GetMapping("/{id}")
    @Operation(
        summary = "Получение типа издания по id",
        description = "Получение данных типа издания по id"
    )
    fun getPublicationType(@PathVariable id: UUID): ResponseEntity<PublicationTypeResponse> {
        log.info {"Request get PublicationType by id: $id"}
        return ResponseEntity.ok(publicationTypeService.getById(id))
    }

    @GetMapping()
    @Operation(
        summary = "Получение всех типов издания",
        description = "Получение данных о всех типах издания"
    )
    fun getAll():ResponseEntity<List<PublicationTypeResponse>> {
        log.info {"Request get all PublicationType"}
        return ResponseEntity.ok(publicationTypeService.getAll())
    }

    @PostMapping()
    @Operation(
        summary = "Добавление типа издания",
        description = "Добавление данных о типе издания"
    )
    fun save(@RequestBody publicationType: PublicationTypeRequest): ResponseEntity<PublicationTypeResponse> {
        log.info {"Request create PublicationType"}
        return ResponseEntity.ok(publicationTypeService.save(publicationType))
    }

    @PutMapping()
    @Operation(
        summary = "Редактирование издания по id",
        description = "Редактирование данных издания по id"
    )
    fun update(@RequestBody publicationType: PublicationTypeEntity): ResponseEntity<PublicationTypeResponse> {
        log.info {"Request update PublicationType by id ${publicationType.id}"}
        return ResponseEntity.ok(publicationTypeService.update(publicationType))
    }

    @DeleteMapping()
    @Operation(
        summary = "Получение всех отношений",
        description = "Удаление типа издания по id, " +
                "удаленный тип издания в записях других таблиц изменится на null"
    )
    fun delete(@RequestParam id: UUID): ResponseEntity<PublicationTypeResponse> {
        log.info {"Request delete PublicationType by id $id"}
        return ResponseEntity.ok(publicationTypeService.delete(id))
    }

}