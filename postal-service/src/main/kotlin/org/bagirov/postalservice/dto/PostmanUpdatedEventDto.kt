package org.bagirov.postalservice.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "DTO, описывающее событие обновления почтальона (postal-service)")
data class PostmanUpdatedEventDto(
    @Schema(description = "ID пользователя (почтальона)")
    val userId: UUID,

    @Schema(description = "Время обновления (millis)")
    val updatedAt: Long
)