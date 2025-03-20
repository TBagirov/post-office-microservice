package org.bagirov.subscriptionservice.client

import mu.KotlinLogging
import org.bagirov.subscriptionservice.dto.response.client.PublicationResponseClient
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class PublicationServiceClientFallbackFactory : FallbackFactory<PublicationServiceClient> {
    override fun create(cause: Throwable): PublicationServiceClient {
        return object : PublicationServiceClient {
            private val log = KotlinLogging.logger {}

            override fun getPublication(publicationId: UUID): PublicationResponseClient {
                log.error(cause) { "Fallback: Publication Service is down. Returning fallback response." }
                throw RuntimeException("Fallback: Publication Service is temporarily unavailable. Please try again later.")
            }
        }
    }
}