package org.bagirov.subscriptionservice.client

import org.bagirov.subscriptionservice.dto.response.client.SubscriberResponseClient
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@FeignClient(
    name = "subscriber-service",
    fallback = SubscriberServiceClientFallbackFactory::class
)
interface SubscriberServiceClient {
    @GetMapping("/api/subscriber/user/{id}")
    fun getSubscriber(
        @PathVariable(name = "id") userId: UUID
    ): SubscriberResponseClient
}

