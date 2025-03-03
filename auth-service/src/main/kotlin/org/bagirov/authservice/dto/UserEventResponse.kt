package org.bagirov.authservice.dto

import java.time.LocalDateTime
import java.util.*

data class UserEventResponse (
    val id: UUID,
//    val name: String,
//    val surname: String,
//    val patronymic: String,
//    val email: String?,
//    val phone: String,
    val createdAt: LocalDateTime,
    val role: String
)