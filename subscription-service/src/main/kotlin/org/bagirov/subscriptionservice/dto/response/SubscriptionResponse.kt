package org.bagirov.subscriptionservice.dto.response

import org.bagirov.subscriptionservice.props.SubscriptionStatus
import java.time.LocalDateTime
import java.util.*
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ с информацией о подписке")
data class SubscriptionResponse(
    @Schema(description = "Уникальный идентификатор подписки")
    val id: UUID,

    @Schema(description = "ID подписчика (subscriberId)")
    val subscriberId: UUID,

    @Schema(description = "ID публикации (издания)")
    val publicationId: UUID,

    @Schema(description = "Дата начала подписки")
    val startDate: LocalDateTime,

    @Schema(description = "Дата окончания подписки")
    val endDate: LocalDateTime,

    @Schema(description = "Продолжительность подписки (в месяцах)")
    val duration: Int,

    @Schema(description = "Статус подписки")
    val status: SubscriptionStatus
)