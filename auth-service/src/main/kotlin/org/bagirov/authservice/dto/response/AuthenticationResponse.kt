package org.bagirov.authservice.dto.response

import java.util.*

data class AuthenticationResponse(
    var id: UUID,
    var username: String,
    var accessToken: String
)
