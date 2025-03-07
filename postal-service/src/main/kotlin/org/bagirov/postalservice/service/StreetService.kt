package org.bagirov.postalservice.service

import org.bagirov.postalservice.dto.request.StreetRequest
import org.bagirov.postalservice.dto.request.StreetUpdateRequest
import org.bagirov.postalservice.dto.response.StreetDistrictResponse
import org.bagirov.postalservice.dto.response.StreetResponse
import org.bagirov.postalservice.entity.RegionEntity
import org.bagirov.postalservice.repository.DistrictRepository
import org.bagirov.postalservice.repository.RegionRepository
import org.bagirov.postalservice.repository.StreetRepository
import org.bagirov.postalservice.utill.convertToEntity
import org.bagirov.postalservice.utill.convertToResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class StreetService(
    private val streetRepository: StreetRepository,
    private val regionRepository: RegionRepository,
    private val districtRepository: DistrictRepository,
    private val regionService: RegionService
) {

    fun getById(id: UUID): StreetResponse =
        streetRepository.findById(id)
            .orElseThrow { NoSuchElementException("Street with ID ${id} not found") }
            .convertToResponseDto()

    fun getAll(): List<StreetResponse> =
        streetRepository.findAll().map { it.convertToResponseDto() }

    @Transactional
    fun getStreetAndDistrict(streetName: String): StreetDistrictResponse {

        // Получаем или создаем улицу по имени
        val street: StreetResponse = streetRepository.findByName(streetName)
            ?.convertToResponseDto()
            ?: save(StreetRequest(streetName))

        val districtRes = districtRepository.findByRegionName(street.regionName!!)
            .orElseThrow {NoSuchElementException("Нет районов для региона ${street.regionName}")}
            .random()

        return StreetDistrictResponse(
            streetId = street.id,
            districtId = districtRes.id!!
        )
    }

    @Transactional
    fun save(streetRequest: StreetRequest): StreetResponse {

        val streetEntity = streetRequest.convertToEntity()
        val nearestRegion: RegionEntity = findNearestRegion(streetEntity.name)

        streetEntity.region = nearestRegion

        val streetSave = streetRepository.save(streetEntity)

        nearestRegion.streets?.add(streetSave)

        return streetSave.convertToResponseDto()
    }

    @Transactional
    fun update(streetRequest: StreetUpdateRequest): StreetResponse {

        // Найти существующую улицу
        val existingStreet = streetRepository.findById(streetRequest.id)
            .orElseThrow { NoSuchElementException("Street with ID ${streetRequest.id} not found") }

        val newRegion: RegionEntity? = regionRepository.findById(streetRequest.regionId).orElse(null)

        // Выполнить обновление в базе данных
        existingStreet.apply {
            region = newRegion
            name = streetRequest.name
        }

        val streetUpdate = streetRepository.save(existingStreet)

        // Обновить связи
        newRegion?.streets?.add(streetUpdate)

        return streetUpdate.convertToResponseDto()
    }

    @Transactional
    fun delete(id: UUID): StreetResponse {

        // Найти существующую улицу
        val existingStreet = streetRepository.findById(id)
            .orElseThrow { NoSuchElementException("Street with ID ${id} not found") }

        // Удалить улицу
        streetRepository.delete(existingStreet)

        return existingStreet.convertToResponseDto()
    }


    private fun findNearestRegion(streetName: String): RegionEntity {
        val regions = regionRepository.findAll()

        if (regions.isEmpty()) {
            return regionService.saveEnt(RegionEntity(name = "Region1"))
        }


        return regions.firstOrNull { region ->
            region.streets?.any { it.name == streetName } ?: false
        } ?: regions.random()
    }
}
