package org.bagirov.subscriberservice.dto.request

data class SubscriberUpdateRequest (
    val building: String,
    val subAddress: String?,
    val streetName: String
)