package org.bagirov.subscriberservice.client

import mu.KotlinLogging
import org.bagirov.subscriberservice.dto.response.client.StreetDistrictResponse
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component

@Component
class PostalServiceClientFallbackFactory : FallbackFactory<PostalServiceClient> {
    private val log = KotlinLogging.logger {}

    override fun create(cause: Throwable?): PostalServiceClient {
        return object : PostalServiceClient {
            override fun getStreetAndDistrict(streetName: String): StreetDistrictResponse {
                log.error { "Fallback: Postal Service is down. Returning fallback response." }
                throw RuntimeException("Fallback: Postal Service is temporarily unavailable. Please try again later.")
            }
        }
    }


}