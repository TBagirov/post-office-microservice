package org.bagirov.subscriptionservice.client


import org.bagirov.subscriptionservice.dto.response.client.PublicationResponseClient
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@FeignClient(
    name = "publication-service",
    fallback = PublicationServiceClientFallbackFactory::class
)
interface PublicationServiceClient {
    @GetMapping("/api/publication/user/{id}")
    fun getPublication(@PathVariable(name = "id") publicationId: UUID): PublicationResponseClient
}

