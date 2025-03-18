package org.bagirov.postalservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO для обновления региона")

data class RegionUpdateRequest(
    @Schema(description = "ID региона")
    val id: UUID,

    @Schema(description = "Новое название региона")
    val name: String,
)