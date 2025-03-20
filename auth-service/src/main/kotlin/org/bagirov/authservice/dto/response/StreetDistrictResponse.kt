package org.bagirov.authservice.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO, возвращающее streetId и districtId (AuthService -> PostalService)")
data class StreetDistrictResponse(
    @Schema(description = "ID улицы (streetId)")
    val streetId: UUID,

    @Schema(description = "ID района (districtId)")
    val districtId: UUID
)