package org.bagirov.authservice.client

import mu.KotlinLogging
import org.bagirov.authservice.dto.response.StreetDistrictResponse
import org.springframework.stereotype.Component

@Component
class PostalServiceClientFallback : PostalServiceClient {
    private val log = KotlinLogging.logger {}

    override fun getStreetAndDistrict(streetName: String): StreetDistrictResponse {
        log.error { "Fallback: Postal Service is down. Returning fallback response." }
        throw RuntimeException("Fallback: Postal Service is temporarily unavailable. Please try again later.")
    }
}