package org.bagirov.subscriberservice.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

/**
 * DTO для события создания подписчика.
 */
@Schema(description = "DTO с информацией о событии создания подписчика")
data class SubscriberEventDto(

    @Schema(description = "ID пользователя", example = "550e8400-e29b-41d4-a716-446655440000")
    val userId: UUID,

    @Schema(description = "ID подписчика", example = "d1d6e0f8-4dfc-11ec-81d3-0242ac130003")
    val subscriberId: UUID
)