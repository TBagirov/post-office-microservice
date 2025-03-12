package org.bagirov.subscriptionservice.dto.response.client

import java.util.*

data class AuthUserResponseClient(
    val userId: UUID,
    val email: String,
    val username: String
)