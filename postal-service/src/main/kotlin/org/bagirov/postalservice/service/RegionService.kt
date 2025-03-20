package org.bagirov.postalservice.service


import mu.KotlinLogging
import org.bagirov.postalservice.dto.request.RegionRequest
import org.bagirov.postalservice.dto.request.RegionUpdateRequest
import org.bagirov.postalservice.dto.response.RegionResponse
import org.bagirov.postalservice.entity.RegionEntity
import org.bagirov.postalservice.repository.RegionRepository
import org.bagirov.postalservice.utill.convertToResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RegionService(
    private val regionRepository: RegionRepository,
    private val districtService: DistrictService
) {

    private val log = KotlinLogging.logger {}

    fun getById(id: UUID): RegionResponse {
        log.info { "Fetching region by ID: $id" }
        return regionRepository.findById(id)
            .orElseThrow {
                log.error { "Region with ID $id not found" }
                NoSuchElementException("Region with ID $id not found")
            }
            .convertToResponseDto()
    }

    fun getAll(): List<RegionResponse> {
        log.info { "Fetching all regions" }
        return regionRepository.findAll().map { it.convertToResponseDto() }
    }

    @Transactional
    fun saveEnt(regionRequest: RegionRequest): RegionEntity {
        log.info { "Saving new region: ${regionRequest.name}" }

        val region = RegionEntity(
            name = regionRequest.name,
        )

        val regionSave = regionRepository.save(region)

        // Добавление региона в таблицу участков (районов)
        districtService.saveOnlyRegion(regionSave)

        log.info { "Region saved successfully: ${regionSave.id}" }
        return regionSave
    }

    @Transactional
    fun save(region: RegionRequest): RegionResponse {
        return saveEnt(region).convertToResponseDto()
    }

    @Transactional
    fun update(region: RegionUpdateRequest): RegionResponse {
        log.info { "Updating region with ID: ${region.id}" }

        val regionId = region.id

        // Найти существующий регион
        val existingRegion = regionRepository.findById(regionId)
            .orElseThrow {
                log.error { "Region with ID $regionId not found" }
                NoSuchElementException("Region with ID $regionId not found")
            }

        existingRegion.name = region.name

        // Выполнить обновление в базе данных
        val saveRegion = regionRepository.save(existingRegion)

        log.info { "Region updated successfully: ${saveRegion.id}" }
        return saveRegion.convertToResponseDto()
    }

    @Transactional
    fun delete(id: UUID): RegionResponse {
        log.info { "Deleting region with ID: $id" }

        // Найти существующий регион
        val existingRegion = regionRepository.findById(id)
            .orElseThrow {
                log.error { "Region with ID $id not found" }
                NoSuchElementException("Region with ID $id not found")
            }

        // Для каждой улицы сбрасываем связь с регионом
        existingRegion.streets?.map { it.region = null }
        // Очищаем коллекцию, чтобы Hibernate не пытался сохранить старые связи
        existingRegion.streets?.clear()

        // Удалить регион
        regionRepository.delete(existingRegion)

        log.info { "Region deleted successfully: $id" }
        return existingRegion.convertToResponseDto()
    }
}
