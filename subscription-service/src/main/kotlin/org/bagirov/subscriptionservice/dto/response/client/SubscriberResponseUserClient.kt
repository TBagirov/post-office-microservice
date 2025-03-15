package org.bagirov.subscriptionservice.dto.response.client

import java.util.UUID
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO (микросервису subscription) с краткой информацией о подписчике по userId")
data class SubscriberResponseUserClient(
    @Schema(description = "ID подписчика (subscriberId)")
    val subscriberId: UUID
)