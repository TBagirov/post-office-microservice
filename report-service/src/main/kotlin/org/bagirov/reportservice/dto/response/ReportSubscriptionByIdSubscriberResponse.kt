package org.bagirov.reportservice.dto.response


import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

/**
 * DTO для отчета по подпискам подписчика
 */
@Schema(description = "DTO с информацией о подписке подписчика для отчета")
data class ReportSubscriptionByIdSubscriberResponse (

    @Schema(description = "Уникальный идентификатор подписки", example = "550e8400-e29b-41d4-a716-446655440000")
    val subscriptionId: UUID,

    @Schema(description = "Уникальный идентификатор издания", example = "a7b64a58-ff33-4991-bbfc-70f84f8c3e7d")
    val publicationId: UUID,

    @Schema(description = "Название издания", example = "Наука и жизнь")
    val title: String,

    @Schema(description = "Тип издания", example = "Журнал")
    val type: String,

    @Schema(description = "Дата начала подписки в формате HH:mm, dd/MM/yyyy", example = "08:30, 01/01/2024")
    val startDate: LocalDateTime,

    @Schema(description = "Дата окончания подписки в формате HH:mm, dd/MM/yyyy", example = "08:30, 01/07/2024")
    val endDate: LocalDateTime,

    @Schema(description = "Цена подписки", example = "599.99")
    val price: BigDecimal
)