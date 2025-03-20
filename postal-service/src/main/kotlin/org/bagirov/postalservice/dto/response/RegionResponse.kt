package org.bagirov.postalservice.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO с информацией о регионе")
data class RegionResponse(
    @Schema(description = "ID региона")
    val id: UUID,

    @Schema(description = "Название региона")
    val name: String,

    @Schema(description = "Список названий улиц")
    val streets: List<String>?,

    @Schema(description = "Список userId почтальонов, связанных с регионом")
    val postmanIds: List<UUID>?
)