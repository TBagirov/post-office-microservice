package org.bagirov.subscriberservice.config


import org.bagirov.subscriberservice.exception.CustomAccessDeniedHandler
import org.bagirov.subscriberservice.props.Role
import org.bagirov.subscriberservice.service.JwtService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
class SecurityConfig(
    private val jwtService: JwtService,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
    private val corsConfigurationSource: CorsConfigurationSource  // Внедряем CORS из CorsConfig
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity) =
        http
            .cors { it.configurationSource(corsConfigurationSource) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/subscriber/v3/api-docs/**",
                    "/api/subscriber/swagger-ui/**",
                    "/api/subscriber/swagger-ui.html"
                ).permitAll()
                it.requestMatchers(HttpMethod.GET, "/api/subscriber/client/**").permitAll()
                it.requestMatchers(HttpMethod.PUT, "/api/subscriber/update").hasAuthority(Role.SUBSCRIBER)
                it.requestMatchers("/api/subscriber/**").hasAnyAuthority(Role.ADMIN)
                it.anyRequest().authenticated()
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtService), BasicAuthenticationFilter::class.java)
            .exceptionHandling { exceptions ->
                exceptions.accessDeniedHandler(customAccessDeniedHandler) // Используем кастомный обработчик
            }
            .build()


}