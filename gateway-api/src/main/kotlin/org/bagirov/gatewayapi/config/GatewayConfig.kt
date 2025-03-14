package org.bagirov.gatewayapi.config

import org.bagirov.gatewayapi.RouteProperties
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

                // Проксирование API-документации (v3/api-docs)
                this.route("${route.id}-swagger") { r ->
                    r.path("${route.path.removeSuffix("/**")}/v3/api-docs")
                        .uri(route.uri)
                }

//                // Проксирование Swagger UI (нужно для работы фронта)
//                this.route("${route.id}-swagger-ui") { r ->
//                    r.path("${route.path.removeSuffix("/**")}/swagger-ui/**")
//                        .uri(route.uri)
//                }
//
//                // Проксирование swagger-ui.html (основная точка входа)
//                this.route("${route.id}-swagger-ui-html") { r ->
//                    r.path("${route.path.removeSuffix("/**")}/swagger-ui.html")
//                        .uri(route.uri)
//                }
//
//                // Проксирование статических ресурсов Swagger UI
//                this.route("${route.id}-swagger-static") { r ->
//                    r.path("${route.path.removeSuffix("/**")}/webjars/**")
//                        .uri(route.uri)
//                }
            }
        }.build()
}