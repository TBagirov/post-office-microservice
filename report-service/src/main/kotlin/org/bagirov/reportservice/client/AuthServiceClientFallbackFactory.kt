package org.bagirov.reportservice.client

import mu.KotlinLogging
import org.bagirov.reportservice.dto.response.client.AuthUserResponseClient
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@Component
class AuthServiceClientFallbackFactory : FallbackFactory<AuthServiceClient> {
    override fun create(cause: Throwable): AuthServiceClient {
        return object : AuthServiceClient {
            private val log = KotlinLogging.logger {}

            override fun getUserDetails(@PathVariable(name = "id") userId: UUID): AuthUserResponseClient {
                log.error(cause) { "Fallback: Publication Service is down. Returning fallback response." }
                throw RuntimeException("Fallback: Publication Service is temporarily unavailable. Please try again later.")
            }
        }
    }
}