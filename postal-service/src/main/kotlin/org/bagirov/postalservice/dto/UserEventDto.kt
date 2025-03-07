package org.bagirov.postalservice.dto

import java.time.LocalDateTime
import java.util.*

data class UserEventDto (
    val id: UUID,
//    val name: String,
//    val surname: String,
//    val patronymic: String,
//    val email: String?,
//    val phone: String,
    val createdAt: LocalDateTime,
    val role: String
)