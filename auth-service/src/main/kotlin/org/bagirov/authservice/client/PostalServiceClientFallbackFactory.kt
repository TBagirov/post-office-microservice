package org.bagirov.authservice.client

import mu.KotlinLogging
import org.bagirov.authservice.dto.response.StreetDistrictResponse
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@Component
class PostalServiceClientFallbackFactory : FallbackFactory<PostalServiceClient> {
    override fun create(cause: Throwable): PostalServiceClient {
        return object : PostalServiceClient {
            private val log = KotlinLogging.logger {}

            override fun getStreetAndDistrict(streetName: String): StreetDistrictResponse {
                log.error { "Fallback: Postal Service is down. Returning fallback response." }
                throw RuntimeException("Fallback: Postal Service is temporarily unavailable. Please try again later.")
            }
        }
    }

}

