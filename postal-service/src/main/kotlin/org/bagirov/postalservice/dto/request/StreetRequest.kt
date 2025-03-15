package org.bagirov.postalservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO для создания новой улицы")
data class StreetRequest(
    @Schema(description = "Название улицы")
    val name: String
)