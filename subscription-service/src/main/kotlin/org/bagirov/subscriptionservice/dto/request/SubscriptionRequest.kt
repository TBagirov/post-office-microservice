package org.bagirov.subscriptionservice.dto.request

import java.util.*
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO для создания новой подписки")
data class SubscriptionRequest(
    @Schema(description = "ID публикации (издания)")
    val publicationId: UUID,

    @Schema(description = "Продолжительность подписки (в месяцах)")
    val duration: Int
)