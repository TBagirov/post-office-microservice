package org.bagirov.subscriberservice.dto.response.client

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO, возвращающее streetId и districtId для подписчика")
data class StreetDistrictResponse(
    @Schema(description = "ID улицы (streetId)")
    val streetId: UUID,

    @Schema(description = "ID района (districtId)")
    val districtId: UUID
)