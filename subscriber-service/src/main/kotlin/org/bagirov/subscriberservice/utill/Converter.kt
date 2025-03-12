package org.bagirov.subscriberservice.utill

import org.bagirov.subscriberservice.entity.SubscriberEntity
import org.bagirov.subscriberservice.dto.response.SubscriberResponse
import org.bagirov.subscriberservice.dto.response.client.SubscriberResponseUserClient

fun SubscriberEntity.convertToResponseDto() = SubscriberResponse(
    id = this.id!!,
    streetId = this.streetId!!,
    districtId = this.districtId!!,
    userId = this.userId,
    building = this.building,
    subAddress = this.subAddress,
)

fun SubscriberEntity.convertToResponseClientDto() = SubscriberResponseUserClient(
    subscriberId = id!!
)
