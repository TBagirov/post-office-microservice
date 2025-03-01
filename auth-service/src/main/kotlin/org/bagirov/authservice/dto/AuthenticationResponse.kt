package org.bagirov.authservice.dto

import java.util.*

data class AuthenticationResponse(
    var id: UUID,
    var username: String,
    var accessToken: String,
    var refreshToken: String
)
