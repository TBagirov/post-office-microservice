package org.bagirov.subscriptionservice.dto.response.client

import java.util.*

data class SubSubscriberResponseClient (
    val id: UUID,
    val userId: UUID,
    val building: String,
    val subAddress: String?,
    val streetId: UUID,
    val districtId: UUID,
)