package org.bagirov.postalservice.dto

import java.util.UUID

data class PostmanUpdatedEventDto(
    val userId: UUID,
    val updatedAt: Long
)
