package org.bagirov.postalservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO для обновления связи почтальона и района")
data class DistrictUpdateRequest(
    @Schema(description = "ID записи (district)")
    val id: UUID,

    @Schema(description = "ID почтальона (postmanId)")
    val postmanId: UUID,

    @Schema(description = "ID региона (regionId)")
    val regionId: UUID
)