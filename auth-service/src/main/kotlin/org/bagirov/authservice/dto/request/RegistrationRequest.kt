package org.bagirov.authservice.dto.request

data class RegistrationRequest (
    val name: String,
    val surname: String,
    val patronymic: String,
    val username: String,
    val password: String,
    val email: String,
    val phone: String,
)
