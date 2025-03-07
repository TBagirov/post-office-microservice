package org.bagirov.subscriberservice.dto.response

import java.util.*

data class SubscriberResponse (
    val id: UUID,
    val userId: UUID,
    val building: String,
    val subAddress: String?,
    val streetId: UUID,
    val districtId: UUID,
)