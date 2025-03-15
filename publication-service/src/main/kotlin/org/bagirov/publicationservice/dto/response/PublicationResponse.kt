package org.bagirov.publicationservice.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.*

@Schema(description = "DTO с информацией о публикации")
data class PublicationResponse(
    @Schema(description = "Уникальный ID публикации")
    val id: UUID,

    @Schema(description = "Уникальный индекс (код) издания")
    val index: String,

    @Schema(description = "Название публикации")
    val title: String,

    @Schema(description = "Автор(ы) публикации")
    val author: String,

    @Schema(description = "Дополнительное описание (аннотация)")
    val description: String?,

    @Schema(description = "URL обложки публикации")
    val coverUrl: String?,

    @Schema(description = "Название типа публикации")
    val publicationType: String,

    @Schema(description = "Цена (за 1 месяц подписки)")
    val price: BigDecimal
)