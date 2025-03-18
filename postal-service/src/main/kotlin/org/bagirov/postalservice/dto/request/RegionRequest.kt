package org.bagirov.postalservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO для создания нового региона")
data class RegionRequest(
    @Schema(description = "Название региона")
    val name: String
)