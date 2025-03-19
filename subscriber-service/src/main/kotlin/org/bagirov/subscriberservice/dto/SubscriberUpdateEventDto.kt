package org.bagirov.subscriberservice.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

/**
 * DTO с информацией о событии обновления подписчика.
 */
@Schema(description = "DTO с информацией о событии обновления подписчика")
data class SubscriberUpdateEventDto(

    @Schema(description = "ID подписчика", example = "550e8400-e29b-41d4-a716-446655440000")
    val subscriberId: UUID,

    @Schema(
        description = "ФИО подписчика (Фамилия Имя Отчество, если есть)",
        example = "Иванов Иван Иванович"
    )
    val fio: String?
)