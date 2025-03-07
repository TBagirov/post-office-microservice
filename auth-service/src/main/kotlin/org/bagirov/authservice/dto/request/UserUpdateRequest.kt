package org.bagirov.authservice.dto.request

data class UserUpdateRequest (
    val name: String?,
    val surname: String?,
    val patronymic: String?,
    val email: String?,
    val phone: String?
)