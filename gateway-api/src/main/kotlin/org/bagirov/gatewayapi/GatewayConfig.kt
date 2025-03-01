package org.bagirov.gatewayapi

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder

@Configuration
class GatewayConfig {

    @Bean
    fun routes(builder: RouteLocatorBuilder, routeProperties: RouteProperties): RouteLocator =
        builder.routes().apply {
            routeProperties.routes.forEach { route ->
                this.route(route.id) { r ->
                    r.path(route.path).uri(route.uri)
                }
            }
        }.build()
}