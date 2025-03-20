package org.bagirov.postalservice.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO, описывающее почтальона")
data class PostmanResponse(
    @Schema(description = "ID почтальона")
    val id: UUID,

    @Schema(description = "ID пользователя (userId)")
    val userId: UUID,

    @Schema(description = "Список названий регионов (regions), где работает почтальон")
    val regions: List<String?>?
)