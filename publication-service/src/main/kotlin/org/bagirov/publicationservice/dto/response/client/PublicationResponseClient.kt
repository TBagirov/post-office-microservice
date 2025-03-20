package org.bagirov.publicationservice.dto.response.client


import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "Упрощённое DTO о публикации, передаваемое по внутреннему клиенту")
data class PublicationResponseClient(
    @Schema(description = "ID публикации")
    val id : UUID,

    @Schema(description = "Цена подписки на 1 месяц")
    val price: BigDecimal,

    @Schema(description = "Название публикации (издания)")
    val title: String
)