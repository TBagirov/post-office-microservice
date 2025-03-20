package org.bagirov.postalservice.dto.request


import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO для обновления улицы")
data class StreetUpdateRequest(
    @Schema(description = "ID улицы")
    val id: UUID,

    @Schema(description = "Новое название улицы")
    val name: String?,

    @Schema(description = "ID региона, к которому относится улица")
    val regionId: UUID?
)