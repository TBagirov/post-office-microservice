package org.bagirov.postalservice.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO, связывающее улицу со случайным районом")
data class StreetDistrictResponse(
    @Schema(description = "ID улицы (streetId)")
    val streetId: UUID,

    @Schema(description = "ID района (districtId)")
    val districtId: UUID
)
