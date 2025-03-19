package org.bagirov.subscriptionservice.utill

import org.bagirov.subscriptionservice.dto.SubscriptionCreatedEvent
import org.bagirov.subscriptionservice.dto.SubscriptionUpdatedEvent
import org.bagirov.subscriptionservice.dto.response.SubscriptionResponse
import org.bagirov.subscriptionservice.entity.SubscriptionEntity

fun SubscriptionEntity.convertToResponseDto() = SubscriptionResponse(
    id = this.id!!,
    subscriberId = subscriberId,
    publicationId = publicationId,
    startDate = this.startDate,
    duration = this.duration,
    endDate = getEndDate(),
    status = this.status
)

fun SubscriptionEntity.convertToEventDto() = SubscriptionCreatedEvent(
    subscriptionId = this.id!!,
    subscriberId = subscriberId,
    publicationId = publicationId,
    startDate = this.startDate,
    duration = this.duration,
    endDate = getEndDate(),
    status = this.status
)
fun SubscriptionEntity.convertToUpdatedEventDto() = SubscriptionUpdatedEvent(
    subscriptionId = this.id!!,
    newStatus = this.status
)