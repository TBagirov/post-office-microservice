package org.bagirov.postalservice.service


import mu.KotlinLogging
import org.bagirov.postalservice.client.AuthServiceClient
import org.bagirov.postalservice.dto.PostmanAssignedEvent
import org.bagirov.postalservice.dto.request.DistrictRequest
import org.bagirov.postalservice.dto.request.DistrictUpdateRequest
import org.bagirov.postalservice.dto.response.DistrictResponse
import org.bagirov.postalservice.entity.DistrictEntity
import org.bagirov.postalservice.entity.PostmanEntity
import org.bagirov.postalservice.entity.RegionEntity
import org.bagirov.postalservice.repository.DistrictRepository
import org.bagirov.postalservice.repository.PostmanRepository
import org.bagirov.postalservice.repository.RegionRepository
import org.bagirov.postalservice.utill.convertToResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException
import java.util.UUID

@Service
class DistrictService(
    private val districtRepository: DistrictRepository,
    private val regionRepository: RegionRepository,
    private val postmanRepository: PostmanRepository,
    private val authServiceClient: AuthServiceClient,
    private val kafkaProducerService: KafkaProducerService
) {

    private val log = KotlinLogging.logger {}

    fun getById(id: UUID): DistrictResponse {
        log.info { "Fetching district by ID: $id" }
        return districtRepository.findById(id)
            .orElseThrow {
                log.error { "District with ID $id not found" }
                NoSuchElementException("District with ID $id not found")
            }
            .convertToResponseDto()
    }

    fun getAll(): List<DistrictResponse> {
        log.info { "Fetching all districts" }
        return districtRepository.findAll().map { it.convertToResponseDto() }
    }

    @Transactional
    fun save(districtRequest: DistrictRequest): DistrictResponse {
        log.info { "Creating new district with regionId: ${districtRequest.regionId}, postmanId: ${districtRequest.postmanId}" }

        val tempRegion: RegionEntity = regionRepository.findById(districtRequest.regionId)
            .orElseThrow {
                log.error { "Region with ID ${districtRequest.regionId} not found" }
                NoSuchElementException("Region with ID ${districtRequest.regionId} not found")
            }

        val tempPostman: PostmanEntity = postmanRepository.findById(districtRequest.postmanId)
            .orElseThrow {
                log.error { "Postman with ID ${districtRequest.postmanId} not found" }
                NoSuchElementException("Postman with ID ${districtRequest.postmanId} not found")
            }

        val district = DistrictEntity(
            region = tempRegion,
            postman = tempPostman
        )

        val districtSave = districtRepository.save(district)

        tempRegion.districts?.add(districtSave)
        tempPostman.districts?.add(districtSave)

        // **Получаем email и username из AuthService**
        val userDetails = authServiceClient.getUserDetails(tempPostman.userId)
        val event = PostmanAssignedEvent(
            email = userDetails.email,
            username = userDetails.username,
            districtName = tempRegion.name
        )

        kafkaProducerService.sendNotificationEvent(event)

        log.info { "District created successfully: ${districtSave.id}" }
        return districtSave.convertToResponseDto()
    }

    @Transactional
    fun saveOnlyRegion(region: RegionEntity) {
        log.info { "Saving district for region: ${region.name}" }
        districtRepository.save(DistrictEntity(region = region))
    }

    @Transactional
    fun update(district: DistrictUpdateRequest): DistrictResponse {
        log.info { "Updating district with ID: ${district.id}" }

        // Найти существующее отношение почтальонов к районам
        val existingDistrict = districtRepository.findById(district.id)
            .orElseThrow {
                log.error { "District with ID ${district.id} not found" }
                NoSuchElementException("District with ID ${district.id} not found")
            }

        val tempRegion: RegionEntity? = regionRepository.findById(district.regionId).orElse(null)
        val tempPostman: PostmanEntity? = postmanRepository.findById(district.postmanId).orElse(null)

        existingDistrict.region = tempRegion
        existingDistrict.postman = tempPostman

        districtRepository.save(existingDistrict)

        tempRegion?.districts?.add(existingDistrict)
        tempPostman?.districts?.add(existingDistrict)

        log.info { "District updated successfully: ${existingDistrict.id}" }
        return existingDistrict.convertToResponseDto()
    }

    @Transactional
    fun delete(id: UUID): DistrictResponse {
        log.info { "Deleting district with ID: $id" }

        val existingDistrict = districtRepository.findById(id)
            .orElseThrow {
                log.error { "District with ID $id not found" }
                NoSuchElementException("District with ID $id not found")
            }

        districtRepository.deleteById(id)

        log.info { "District deleted successfully: $id" }
        return existingDistrict.convertToResponseDto()
    }
}