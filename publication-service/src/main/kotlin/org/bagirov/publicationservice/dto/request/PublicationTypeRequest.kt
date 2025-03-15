package org.bagirov.publicationservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO для создания нового типа публикации")
data class PublicationTypeRequest(
    @Schema(description = "Название типа")
    val type: String
)