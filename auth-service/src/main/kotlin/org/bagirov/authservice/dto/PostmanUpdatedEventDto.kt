package org.bagirov.authservice.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Событие обновления почтальона (AuthService)")
data class PostmanUpdatedEventDto(
    @Schema(description = "ID пользователя (почтальона)")
    val userId: UUID,

    @Schema(description = "Время обновления (в виде epoch millis)")
    val updatedAt: Long
)