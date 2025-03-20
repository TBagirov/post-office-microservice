package org.bagirov.reportservice.dto.response


import io.swagger.v3.oas.annotations.media.Schema
import org.bagirov.reportservice.props.SubscriptionStatus
import java.time.LocalDateTime
import java.util.*

/**
 * DTO для отчета по подпискам
 */
@Schema(description = "DTO с информацией о подписке для отчета")
data class ReportSubscriptionResponse (

    @Schema(description = "Уникальный идентификатор подписки", example = "550e8400-e29b-41d4-a716-446655440000")
    val subscriptionId: UUID,

    @Schema(description = "Уникальный идентификатор подписчика", example = "d1d6e0f8-4dfc-11ec-81d3-0242ac130003")
    val subscriberId: UUID,

    @Schema(description = "Уникальный идентификатор издания", example = "a7b64a58-ff33-4991-bbfc-70f84f8c3e7d")
    val publicationId: UUID,

    @Schema(description = "ФИО подписчика", example = "Иванов Иван Иванович")
    val fioSubscriber: String,

    @Schema(description = "Название издания", example = "Наука и жизнь")
    val titlePublication: String,

    @Schema(description = "Дата начала подписки в формате HH:mm, dd/MM/yyyy", example = "08:30, 01/01/2024")
    val startDateSubscription: LocalDateTime,

    @Schema(description = "Дата окончания подписки в формате HH:mm, dd/MM/yyyy", example = "08:30, 01/07/2024")
    val endDateSubscription: LocalDateTime,

    @Schema(description = "Статус подписки", example = "ACTIVE")
    val statusSubscription: SubscriptionStatus
)