package org.bagirov.postalservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.bagirov.postalservice.dto.response.PostmanResponse
import org.bagirov.postalservice.entity.PostmanEntity
import org.bagirov.postalservice.service.PostmanService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@CrossOrigin(origins = arrayOf("http://localhost:3000"))
@RestController
@RequestMapping("/api/postal/postman")
@Tag(name = "PostmanController", description = "Контроллер для взаимодействия с почтальонами")
class PostmanController (
    private val postmanService: PostmanService
){
    private val log = KotlinLogging.logger {}

    @GetMapping("/{id}")
    @Operation(
        summary = "Получение почтальона по id",
        description = "Получение данных о почтальона по id"
    )
    fun getPostman(@PathVariable id: UUID): ResponseEntity<PostmanResponse> {
        log.info { "Request Postman by id: $id" }
        return ResponseEntity.ok(postmanService.getById(id))
    }

    @GetMapping()
    @Operation(
        summary = "Получение всех почтальонов",
        description = "Получение данных о всех почтальонах"
    )
    fun getAll():ResponseEntity<List<PostmanResponse>> {
        log.info { "Request all Postman" }
        return ResponseEntity.ok(postmanService.getAll())
    }


    @PutMapping()
    @Operation(
        summary = "Редактирование почтальона по id",
        description = "Редактирование данных о почтальоне по id"
    )
    fun update(@RequestBody postman: PostmanEntity): ResponseEntity<PostmanResponse> {
        log.info { "Request update postman by id: ${postman.id}" }
        return ResponseEntity.ok(postmanService.update(postman))
    }



}