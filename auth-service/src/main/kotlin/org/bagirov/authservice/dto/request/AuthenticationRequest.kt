package org.bagirov.authservice.dto.request

data class AuthenticationRequest(
    var username: String,
    var password: String
)
