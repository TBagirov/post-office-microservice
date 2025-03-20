package org.bagirov.subscriptionservice.dto.response.client

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "DTO с информацией о публикации, полученной из publication-service")
data class PublicationResponseClient(
    @Schema(description = "ID публикации (издания)")
    val id: UUID,

    @Schema(description = "Цена издания (за 1 месяц подписки)")
    val price: BigDecimal,

    @Schema(description = "Название публикации (издания)")
    val title: String
)