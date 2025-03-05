package org.bagirov.subscriberservice.dto.request


data class SubscriberRequest (
    val building: String,
    val subAddress: String?,
    val street: String
)