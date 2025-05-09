package org.bagirov.postalservice.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.bagirov.postalservice.dto.request.StreetRequest
import org.bagirov.postalservice.dto.request.StreetUpdateRequest
import org.bagirov.postalservice.dto.response.StreetDistrictResponse
import org.bagirov.postalservice.dto.response.StreetResponse
import org.bagirov.postalservice.service.StreetService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@CrossOrigin(origins = arrayOf("http://localhost:3000"))
@RestController
@RequestMapping("/api/postal/street")
@Tag(name = "StreetController", description = "Контроллер для взаимодействия с улицами")
class StreetController(
    private val streetService: StreetService
) {
    private val log = KotlinLogging.logger {}

    @GetMapping("/{id}")
    @Operation(
        summary = "Получение улицы по id",
        description = "Получение данных о улице по id"
    )
    fun getStreet(@PathVariable id: UUID):ResponseEntity<StreetResponse>{
        log.info {"Request get Street by id: $id"}
        return ResponseEntity.ok(streetService.getById(id))
    }

    @GetMapping()
    @Operation(
        summary = "Получение всех улиц",
        description = "Получение данных о всех улицах"
    )
    fun getAll():ResponseEntity<List<StreetResponse>> {
        log.info {"Request get all Street"}
        return ResponseEntity.ok(streetService.getAll())
    }

    @PostMapping()
    @Operation(
        summary = "Добавление улицы",
        description = "Добавление данных улицы, " +
                "улица определяется в какой-то регион автоматически"
    )
    fun save(@RequestBody streetRequest: StreetRequest): ResponseEntity<StreetResponse> {
        log.info {"Request create Street"}
        return ResponseEntity.ok(streetService.save(streetRequest))
    }


    @Value("\${internal.api-secret}")
    private lateinit var apiSecret: String

    @GetMapping("/street-info")
    @Operation(
        summary = "Получить ID улицы и участка",
        description = "Возвращает ID улицы и ID участка обслуживаемый почтальоном"
    )
    fun getStreetAndDistrict(
        @Parameter(hidden = true) @RequestHeader(value = "X-Internal-Call", required = false) secret: String?,
        @RequestParam streetName: String
    ): ResponseEntity<StreetDistrictResponse> {
        if (secret != apiSecret) {
            log.warn { "Forbidden access to /street-info with $streetName. Invalid secret: $secret" }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        log.info { "Received request for street info: $streetName" }
        return ResponseEntity.ok(streetService.getStreetAndDistrict(streetName))
    }


    @PutMapping()
    @Operation(
        summary = "Редактирование улицы по id",
        description = "Редактирование данных улицы по id"
    )
    fun update(@RequestBody streetUpdate: StreetUpdateRequest): ResponseEntity<StreetResponse> {
        log.info {"Request update Street by id: ${streetUpdate.id}"}
        return ResponseEntity.ok(streetService.update(streetUpdate))
    }

    @DeleteMapping()
    @Operation(
        summary = "Удаление улицы по id",
        description = "Удаление улицы по id, " +
                "удаленная улица в записях других таблиц изменится на null"
    )
    fun delete(@RequestParam id: UUID): ResponseEntity<StreetResponse> {
        log.info {"Delete Street by id: $id"}
        return ResponseEntity.ok(streetService.delete(id))
    }

}