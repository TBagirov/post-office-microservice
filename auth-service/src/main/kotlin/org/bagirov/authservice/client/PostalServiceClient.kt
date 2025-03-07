package org.bagirov.authservice.client

import org.bagirov.authservice.dto.response.StreetDistrictResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "postal-service",
    fallback = PostalServiceClientFallback::class
)
interface PostalServiceClient {
    @GetMapping("/api/postal/street/street-info")
    fun getStreetAndDistrict(@RequestParam streetName: String): StreetDistrictResponse
}