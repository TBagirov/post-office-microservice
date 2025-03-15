package org.bagirov.subscriberservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsWebFilter {
        val corsConfig = CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("Authorization", "Content-Type", "X-Internal-Call")
            exposedHeaders = listOf("Authorization")  // Чтобы клиент мог получать этот заголовок
            allowCredentials = true
            maxAge = 3600  // Кэширование CORS-политики на 1 час
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)

        return CorsWebFilter(source)
    }
}