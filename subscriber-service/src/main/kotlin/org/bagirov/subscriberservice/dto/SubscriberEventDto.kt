package org.bagirov.subscriberservice.dto

import java.util.*

data class SubscriberEventDto(
    val userId: UUID,
    val subscriberId: UUID
)
