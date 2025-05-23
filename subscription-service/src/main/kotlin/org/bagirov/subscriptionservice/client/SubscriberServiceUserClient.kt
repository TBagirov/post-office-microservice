package org.bagirov.subscriptionservice.client

import org.bagirov.subscriptionservice.dto.response.client.SubscriberResponseClient
import org.bagirov.subscriptionservice.dto.response.client.SubscriberResponseUserClient
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@FeignClient(
    name = "subscriber-service",
    fallback = SubscriberServiceUserClientFallbackFactory::class
)
interface SubscriberServiceUserClient {

    @GetMapping("/api/subscriber/client/{id}")
    fun getSubscriber(
        @PathVariable(name = "id") subscriberId: UUID
    ): SubscriberResponseClient

    @GetMapping("/api/subscriber/client/user/{id}")
    fun getSubscriberByUserId(
        @PathVariable(name = "id") userId: UUID
    ): SubscriberResponseUserClient
}

