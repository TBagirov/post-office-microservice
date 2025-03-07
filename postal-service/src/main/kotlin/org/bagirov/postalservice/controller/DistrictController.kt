package org.bagirov.postalservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.bagirov.postalservice.dto.request.DistrictRequest
import org.bagirov.postalservice.dto.request.DistrictUpdateRequest
import org.bagirov.postalservice.dto.response.DistrictResponse
import org.bagirov.postalservice.service.DistrictService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import mu.KotlinLogging

@CrossOrigin(origins = arrayOf("http://localhost:3000"))
@RestController
@RequestMapping("api/postal/district")
@Tag(name = "DistrictController", description = "Контроллер для взаимодействия с отношением почтальонов к участкам")
class DistrictController(
    private val districtService: DistrictService
) {
    private val log = KotlinLogging.logger {}

    @GetMapping("/{id}")
    @Operation(
        summary = "Получение отношения по id",
        description = "Получение отношения почтальона к участку по id отношения"
    )
    fun getDistrict(@PathVariable id: UUID):ResponseEntity<DistrictResponse>{
        log.info { "Request get District by id: $id" }
        return ResponseEntity.ok(districtService.getById(id))
    }

    @GetMapping()
    @Operation(
        summary = "Получение всех отношений",
        description = "Получение всех отношений почтальонов к участкам"
    )
    fun getAll():ResponseEntity<List<DistrictResponse>> {
        log.info { "Request get all District" }
        return ResponseEntity.ok(districtService.getAll())
    }

    @PostMapping()
    @Operation(
        summary = "Добавление отношения",
        description = "Добавление отношения почтальона к участку"
    )
    fun save(@RequestBody district: DistrictRequest): ResponseEntity<DistrictResponse> {
        log.info {"Request create District"}
        return ResponseEntity.ok(districtService.save(district))
    }

    @PutMapping()
    @Operation(
        summary = "Редактирование отношения",
        description = "Редактирование отношения почтальона к участкам"
    )
    fun update(@RequestBody district: DistrictUpdateRequest): ResponseEntity<DistrictResponse> {
        log.info {"Request updating District by id: ${district.id}"}
        return ResponseEntity.ok(districtService.update(district))
    }

    @DeleteMapping()
    @Operation(
        summary = "Удаление отношения по id",
        description = "Удаление отношения почтальона к участку по id, " +
                "удаленное отношение в записях других таблиц изменится на null"
    )
    fun delete(@RequestParam id: UUID): ResponseEntity<DistrictResponse> {
        log.info("Request delete District by id: $id")
        return ResponseEntity.ok(districtService.delete(id))
    }

}