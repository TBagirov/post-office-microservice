package org.bagirov.postalservice.utill

import org.bagirov.postalservice.dto.request.StreetRequest
import org.bagirov.postalservice.dto.response.DistrictResponse
import org.bagirov.postalservice.dto.response.PostmanResponse
import org.bagirov.postalservice.dto.response.RegionResponse
import org.bagirov.postalservice.dto.response.StreetResponse
import org.bagirov.postalservice.entity.DistrictEntity
import org.bagirov.postalservice.entity.PostmanEntity
import org.bagirov.postalservice.entity.RegionEntity
import org.bagirov.postalservice.entity.StreetEntity


fun PostmanEntity.convertToResponseDto() = PostmanResponse(
    id = this.id!!,
    userId = this.userId,
    regions = this.districts?.map {  it.region?.name }
)

fun StreetEntity.convertToResponseDto() = StreetResponse(
    id = this.id!!,
    name = this.name,
    regionName = region?.name,
)

fun StreetRequest.convertToEntity() = StreetEntity(
    id = null,
    name = this.name,
    region = null
)

fun RegionEntity.convertToResponseDto() = RegionResponse(
    id = this.id!!,
    name = this.name,
    streets = this.streets?.map { it.name },
    postmanIds = this.districts?.mapNotNull { it.postman?.userId}
)


fun DistrictEntity.convertToResponseDto() = DistrictResponse(
    id = this.id!!,
    postmanId = this.postman?.id,
    regionName = this.region?.name
)