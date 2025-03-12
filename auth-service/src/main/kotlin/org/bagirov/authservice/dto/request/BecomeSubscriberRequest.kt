package org.bagirov.authservice.dto.request

data class BecomeSubscriberRequest (
    val streetName: String,
    val building: String,
    val subAddress: String?
)