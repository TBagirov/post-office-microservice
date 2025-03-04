package org.bagirov.authservice.dto.request

import java.util.*

data class UserUpdateRequest (
    val userId: UUID,
    val name: String?,
    val surname: String?,
    val patronymic: String?,
    val email: String?,
    val phone: String?
)