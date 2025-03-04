package org.bagirov.authservice.dto.response

import java.time.LocalDateTime
import java.util.*

data class UserResponse(
    val id: UUID,
    val name: String,
    val surname: String,
    val patronymic: String,
    val username: String,
    val password: String,
    val email: String,
    val phone: String,
    val createdAt: LocalDateTime,
    val role: String
)
