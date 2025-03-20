package org.bagirov.postalservice.dto.response


import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO, описывающее связь почтальона и района")
data class DistrictResponse(
    @Schema(description = "ID записи (district)")
    val id: UUID,

    @Schema(description = "ID почтальона (postmanId)")
    val postmanId: UUID?,

    @Schema(description = "Название региона")
    val regionName: String?
)