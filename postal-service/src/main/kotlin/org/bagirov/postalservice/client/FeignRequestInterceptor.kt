package org.bagirov.postalservice.client

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FeignRequestInterceptor(
    @Value("\${internal.api-secret}") private val apiSecret: String
) : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        template.header("X-Internal-Call", apiSecret)
    }
}