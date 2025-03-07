package org.bagirov.postalservice.service


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

    fun getById(id: UUID): RegionResponse =
        regionRepository.findById(id)
            .orElseThrow { NoSuchElementException("Region with ID ${id} not found") }
            .convertToResponseDto()

    fun getAll(): List<RegionResponse> =
        regionRepository.findAll().map { it.convertToResponseDto() }

    @Transactional
    fun saveEnt(region: RegionEntity): RegionEntity {

        val regionSave = regionRepository.save(region)

        districtService.saveOnlyRegion(regionSave)

        return region
    }

    @Transactional
    fun save(region: RegionEntity): RegionResponse {
        return saveEnt(region).convertToResponseDto()
    }

    @Transactional
    fun update(region: RegionEntity): RegionResponse {
        // Проверяем, что у региона есть ID; если нет – выбрасываем исключение
        val regionId = region.id ?: throw IllegalArgumentException("Region id must not be null")

        // Найти существующий регион
        val existingRegion = regionRepository.findById(regionId)
            .orElseThrow { NoSuchElementException("Region with ID ${region.id} not found") }

        existingRegion.name = region.name

        // Выполнить обновление в базе данных
        val saveRegion = regionRepository.save(existingRegion)

        return saveRegion.convertToResponseDto()
    }

    @Transactional
    fun delete(id: UUID): RegionResponse {
        // Найти существующий регион
        val existingRegion = regionRepository.findById(id)
            .orElseThrow { NoSuchElementException("Region with ID ${id} not found") }

        // Для каждой улицы сбрасываем связь с регионом
        existingRegion.streets?.map { it.region = null }
        // Очищаем коллекцию, чтобы Hibernate не пытался сохранить старые связи
        existingRegion.streets?.clear()

        // Удалить регион
        regionRepository.delete(existingRegion)

        return existingRegion.convertToResponseDto()
    }

}
