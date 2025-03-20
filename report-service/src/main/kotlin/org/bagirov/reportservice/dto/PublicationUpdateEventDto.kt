package org.bagirov.publicationservice.dto.request.update

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.*

@Schema(description = "DTO для обновления данных публикации")
data class PublicationUpdateEventDto(
    @Schema(description = "ID публикации")
    val id: UUID,

    @Schema(description = "Новый индекс (код) издания")
    val index: String?,

    @Schema(description = "Новое название")
    val title: String?,

    @Schema(description = "Новый(е) автор(ы)")
    val author: String?,

    @Schema(description = "Новый тип издания (строка)")
    val typeName: String?,

    @Schema(description = "Новая цена (BigDecimal)")
    val price: BigDecimal?
)