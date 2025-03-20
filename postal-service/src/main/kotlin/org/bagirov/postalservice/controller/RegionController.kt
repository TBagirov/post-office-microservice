package org.bagirov.postalservice.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.bagirov.postalservice.dto.request.RegionRequest
import org.bagirov.postalservice.dto.request.RegionUpdateRequest
import org.bagirov.postalservice.dto.response.RegionResponse
import org.bagirov.postalservice.service.RegionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@CrossOrigin(origins = arrayOf("http://localhost:3000"))
@RestController
@RequestMapping("/api/postal/region")
@Tag(name = "RegionController", description = "Контроллер для взаимодействия с регионами")
class RegionController(
    val regionService: RegionService
) {
    private val log = KotlinLogging.logger {}

    @GetMapping("/{id}")
    @Operation(
        summary = "Получение региона по id",
        description = "Получение данных региона по id"
    )
    fun getRegion(@PathVariable id: UUID): ResponseEntity<RegionResponse> {
        log.info { "Request get Region by id $id" }
        return ResponseEntity.ok(regionService.getById(id))
    }

    @GetMapping()
    @Operation(
        summary = "Получение всех регионов",
        description = "Получение всех данных о регионах"
    )
    fun getAll():ResponseEntity<List<RegionResponse>> {
        log.info {"Request get all Region"}
        return ResponseEntity.ok(regionService.getAll())
    }

    @PostMapping()
    @Operation(
        summary = "Добавление региона",
        description = "Добавление данных региона"
    )
    fun save(@RequestBody region: RegionRequest): ResponseEntity<RegionResponse> {
        log.info { "Request create Region" }
        return ResponseEntity.ok(regionService.save(region))
    }

    @PutMapping()
    @Operation(
        summary = "Редактирование региона по id",
        description = "Редактирование данных региона по id"
    )
    fun update(@RequestBody region: RegionUpdateRequest): ResponseEntity<RegionResponse> {
        log.info {"Request update Region by id ${region.id}"}
        return ResponseEntity.ok(regionService.update(region))
    }

    @DeleteMapping()
    @Operation(
        summary = "Удаление региона",
        description = "Удаление региона по id, " +
                "удаленный регион в записях других таблиц изменится на null"
    )
    fun delete(@RequestParam id: UUID): ResponseEntity<RegionResponse> {
        log.info {"Request delete Region by id $id"}
        return ResponseEntity.ok(regionService.delete(id))
    }


}