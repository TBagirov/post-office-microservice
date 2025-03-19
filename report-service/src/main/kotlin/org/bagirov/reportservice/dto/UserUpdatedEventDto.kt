package org.bagirov.reportservice.dto

import java.time.LocalDateTime
import java.util.*

data class UserUpdatedEventDto(
    val userId: UUID,

    val name: String,

    val surname: String,

    val patronymic: String,

    val email: String,

    val phone: String,

    val updatedAt: LocalDateTime?,

    val role: String
)
