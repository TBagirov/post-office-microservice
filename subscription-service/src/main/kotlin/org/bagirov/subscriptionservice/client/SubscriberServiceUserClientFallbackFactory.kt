package org.bagirov.subscriptionservice.client

import mu.KotlinLogging
import org.bagirov.subscriptionservice.dto.response.client.SubSubscriberResponseClient
import org.bagirov.subscriptionservice.dto.response.client.SubscriberResponseClient
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.stereotype.Component
import java.util.*


@Component
class SubscriberServiceUserClientFallbackFactory : FallbackFactory<SubscriberServiceUserClient> {
    override fun create(cause: Throwable): SubscriberServiceUserClient {
        return object : SubscriberServiceUserClient {
            private val log = KotlinLogging.logger {}

            override fun getSubscriberSub(subscriberId: UUID): SubSubscriberResponseClient {
                log.error(cause) { "Fallback: Subscriber Service is down. Returning fallback response." }
                throw RuntimeException("Fallback: Subscriber Service is temporarily unavailable. Please try again later.")
            }

            override fun getSubscriber(userId: UUID): SubscriberResponseClient {
                log.error(cause) { "Fallback: Subscriber Service is down. Returning fallback response." }
                throw RuntimeException("Fallback: Subscriber Service is temporarily unavailable. Please try again later.")
            }
        }
    }
}