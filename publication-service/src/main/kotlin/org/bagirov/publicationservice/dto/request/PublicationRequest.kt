package org.bagirov.publicationservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "DTO для создания новой публикации (издания)")
data class PublicationRequest(
    @Schema(description = "Индекс (код) издания")
    val index: String,

    @Schema(description = "Название публикации")
    val title: String,

    @Schema(description = "Автор(ы) публикации")
    val author: String,

    @Schema(description = "Описание (аннотация)")
    val description: String?,

    @Schema(description = "URL обложки (при наличии)")
    val coverUrl: String?,

    @Schema(description = "Название типа издания")
    val type: String,

    @Schema(description = "Цена за 1 месяц подписки (BigDecimal)")
    val price: BigDecimal
)