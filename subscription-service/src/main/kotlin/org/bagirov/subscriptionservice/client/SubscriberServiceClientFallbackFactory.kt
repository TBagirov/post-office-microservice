package org.bagirov.subscriptionservice.client

import mu.KotlinLogging
import org.bagirov.subscriptionservice.dto.response.client.SubscriberResponseClient
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component
import java.util.*


@Component
class SubscriberServiceClientFallbackFactory : FallbackFactory<SubscriberServiceClient> {
    override fun create(cause: Throwable): SubscriberServiceClient {
        return object : SubscriberServiceClient {
            private val log = KotlinLogging.logger {}

            override fun getSubscriber(userId: UUID): SubscriberResponseClient {
                log.error(cause) { "Fallback: Subscriber Service is down. Returning fallback response." }
                throw RuntimeException("Fallback: Subscriber Service is temporarily unavailable. Please try again later.")
            }
        }
    }
}