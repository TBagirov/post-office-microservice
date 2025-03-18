package org.bagirov.postalservice.service

import mu.KotlinLogging
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

    private val log = KotlinLogging.logger {}

    fun getById(id: UUID): StreetResponse {
        log.info { "Fetching street by ID: $id" }
        return streetRepository.findById(id)
            .orElseThrow { NoSuchElementException("Street with ID $id not found") }
            .convertToResponseDto()
    }

    fun getAll(): List<StreetResponse> {
        log.info { "Fetching all streets" }
        return streetRepository.findAll().map { it.convertToResponseDto() }
    }

    @Transactional
    fun getStreetAndDistrict(streetName: String): StreetDistrictResponse {
        log.info { "Fetching street and district for street name: $streetName" }

        val street: StreetResponse = streetRepository.findByName(streetName)
            ?.convertToResponseDto()
            ?: save(StreetRequest(streetName))

        val districtRes = districtRepository.findByRegionName(street.regionName!!)
            .orElseThrow { NoSuchElementException("No districts found for region ${street.regionName}") }
            .random()

        return StreetDistrictResponse(
            streetId = street.id,
            districtId = districtRes.id!!
        )
    }

    @Transactional
    fun save(streetRequest: StreetRequest): StreetResponse {
        log.info { "Creating a new street: ${streetRequest.name}" }

        val streetEntity = streetRequest.convertToEntity()
        val nearestRegion: RegionEntity = findNearestRegion(streetEntity.name)

        streetEntity.region = nearestRegion

        val streetSave = streetRepository.save(streetEntity)

        nearestRegion.streets?.add(streetSave)

        log.info { "Street created successfully: ${streetSave.id}" }
        return streetSave.convertToResponseDto()
    }

    @Transactional
    fun update(streetRequest: StreetUpdateRequest): StreetResponse {
        log.info { "Updating street with ID: ${streetRequest.id}" }

        val existingStreet = streetRepository.findById(streetRequest.id)
            .orElseThrow { NoSuchElementException("Street with ID ${streetRequest.id} not found") }

        val newRegion: RegionEntity? = regionRepository.findById(streetRequest.regionId).orElse(null)

        existingStreet.apply {
            region = newRegion
            name = streetRequest.name
        }

        val streetUpdate = streetRepository.save(existingStreet)

        newRegion?.streets?.add(streetUpdate)

        log.info { "Street updated successfully: ${streetUpdate.id}" }
        return streetUpdate.convertToResponseDto()
    }

    @Transactional
    fun delete(id: UUID): StreetResponse {
        log.info { "Deleting street with ID: $id" }

        val existingStreet = streetRepository.findById(id)
            .orElseThrow { NoSuchElementException("Street with ID $id not found") }

        streetRepository.delete(existingStreet)

        log.info { "Street deleted successfully: $id" }
        return existingStreet.convertToResponseDto()
    }

    private fun findNearestRegion(streetName: String): RegionEntity {
        log.info { "Finding the nearest region for street: $streetName" }

        val regions = regionRepository.findAll()

        if (regions.isEmpty()) {
            log.warn { "No regions found, creating default region 'Region1'" }
            return regionService.saveEnt(RegionEntity(name = "Region1"))
        }

        return regions.firstOrNull { region ->
            region.streets?.any { it.name == streetName } ?: false
        } ?: regions.random().also {
            log.info { "Assigned street to region: ${it.name}" }
        }
    }
}
