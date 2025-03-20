package org.bagirov.reportservice.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

/**
 * DTO для отчета по изданиям
 */
@Schema(description = "DTO с информацией о публикации для отчета")
data class ReportPublicationResponse (

    @Schema(description = "Уникальный идентификатор издания", example = "a7b64a58-ff33-4991-bbfc-70f84f8c3e7d")
    val publicationId: UUID,

    @Schema(description = "Книжный индекс издания", example = "12345-ABC")
    val index: String,

    @Schema(description = "Название издания", example = "Наука и жизнь")
    val title: String,

    @Schema(description = "Автор(ы) издания", example = "Иван Петров")
    val author: String,

    @Schema(description = "Тип издания", example = "Журнал")
    val type: String,

    @Schema(description = "Цена издания", example = "599.99")
    val price: Int,

    @Schema(description = "Количество подписчиков", example = "125")
    val countSubscriber: Int
)