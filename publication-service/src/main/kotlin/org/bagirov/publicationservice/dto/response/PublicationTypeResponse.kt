package org.bagirov.publicationservice.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "DTO, описывающее тип издания (publication-type)")
data class PublicationTypeResponse(
    @Schema(description = "ID типа издания")
    val id: UUID,

    @Schema(description = "Название типа издания")
    val type: String,

    @Schema(description = "Коллекция (список) публикаций данного типа")
    val publications: List<PublicationResponse>?
)