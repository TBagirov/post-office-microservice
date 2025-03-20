package org.bagirov.subscriberservice.dto.response.client

import java.util.UUID
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO, возвращающее subscriberId по userId")
data class SubscriberResponseUserClient(
    @Schema(description = "Уникальный идентификатор подписчика (subscriberId)")
    val subscriberId: UUID
)