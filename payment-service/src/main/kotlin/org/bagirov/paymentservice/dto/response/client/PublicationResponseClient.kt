package org.bagirov.paymentservice.dto.response.client

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "DTO, получаемое из publication-service для расчёта стоимости подписки")
data class PublicationResponseClient(
    @Schema(description = "ID публикации")
    val id: UUID,

    @Schema(description = "Цена за 1 месяц подписки")
    val price: BigDecimal
)