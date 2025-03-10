package org.bagirov.postalservice.service


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

    fun getById(id: UUID): DistrictResponse = districtRepository.findById(id)
        .orElseThrow { NoSuchElementException("District with ID ${id} not found") }
        .convertToResponseDto()

    fun getAll(): List<DistrictResponse> = districtRepository.findAll().map { it.convertToResponseDto() }

    @Transactional
    fun save(districtRequest: DistrictRequest): DistrictResponse {

        val tempRegion: RegionEntity? = regionRepository.findById(districtRequest.regionId)
            .orElseThrow { NoSuchElementException("District with ID ${districtRequest.regionId} not found") }

        val tempPostman: PostmanEntity? = postmanRepository.findById(districtRequest.postmanId)
            .orElseThrow { NoSuchElementException("Postman with ID ${districtRequest.postmanId} not found") }

        val district = DistrictEntity(
            region = tempRegion,
            postman = tempPostman
        )

        districtRepository.save(district)

        tempRegion?.districts?.add(district)
        tempPostman?.districts?.add(district)

        // **Получаем email и username из AuthService**
        val userDetails = authServiceClient.getUserDetails(tempPostman!!.userId)

        val event = PostmanAssignedEvent(
            email = userDetails.email,
            username = userDetails.username,
            districtName = tempRegion!!.name
        )

        kafkaProducerService.sendNotificationEvent(event)

        return district.convertToResponseDto()
    }

    @Transactional
    fun saveOnlyRegion(region: RegionEntity) {
        districtRepository.save(DistrictEntity(region = region))
    }

    @Transactional
    fun update(district: DistrictUpdateRequest): DistrictResponse {
        // Найти существующее отношение почтальонов к районам
        val existingDistrict = districtRepository.findById(district.id)
            .orElseThrow { NoSuchElementException("District with ID ${district.id} not found") }

        val tempRegion: RegionEntity? = regionRepository.findById(district.regionId).orElse(null)
        val tempPostman: PostmanEntity? = postmanRepository.findById(district.postmanId).orElse(null)

        existingDistrict.region = tempRegion
        existingDistrict.postman = tempPostman

        // Выполнить обновление в базе данных
        districtRepository.save(existingDistrict)

        // Обновить связи
        tempRegion?.districts?.add(existingDistrict)
        tempPostman?.districts?.add(existingDistrict)

        return existingDistrict.convertToResponseDto()
    }

    @Transactional
    fun delete(id: UUID): DistrictResponse {
        // Найти существующее отношение почтальонов к районам
        val existingDistrict = districtRepository.findById(id)
            .orElseThrow { NoSuchElementException("District with ID $id not found") }

        // Удалить отношение почтальонов к районам
        districtRepository.deleteById(id)

        // Преобразовать удалённое отношение почтальонов к районам в DTO и вернуть
        return existingDistrict.convertToResponseDto()
    }

}