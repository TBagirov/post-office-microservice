package org.bagirov.authservice.dto

import java.util.*

data class UserBecomeSubscriberEventDto(
    val userId: UUID,
    val streetId: UUID,
    val districtId: UUID,
    val building: String,
    val subAddress: String?,
    val createdAt: Long
)