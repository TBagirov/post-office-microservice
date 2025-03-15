package org.bagirov.postalservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO для создания новой связи почтальона и района")
data class DistrictRequest(
    @Schema(description = "ID почтальона")
    val postmanId: UUID,

    @Schema(description = "ID региона")
    val regionId: UUID
)