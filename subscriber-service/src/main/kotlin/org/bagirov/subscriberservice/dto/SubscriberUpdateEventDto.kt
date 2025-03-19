package org.bagirov.subscriberservice.dto

import java.util.*

data class SubscriberUpdateEventDto(
    val subscriberId: UUID,
    val fio: String?
)
