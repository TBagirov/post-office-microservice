package org.bagirov.authservice.dto

data class AuthenticationRequest(
    var username: String,
    var password: String
)
