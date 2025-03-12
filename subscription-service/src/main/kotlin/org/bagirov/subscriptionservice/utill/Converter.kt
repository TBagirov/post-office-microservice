package org.bagirov.subscriptionservice.utill

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