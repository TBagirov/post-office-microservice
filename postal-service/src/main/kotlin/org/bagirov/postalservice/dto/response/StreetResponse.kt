package org.bagirov.postalservice.dto.response


import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO с информацией об улице")
data class StreetResponse(
    @Schema(description = "Уникальный ID улицы")
    val id: UUID,

    @Schema(description = "Название улицы")
    val name: String,

    @Schema(description = "Название региона, к которому относится данная улица (если есть)")
    val regionName: String?
)