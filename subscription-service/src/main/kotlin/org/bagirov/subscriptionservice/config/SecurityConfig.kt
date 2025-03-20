package org.bagirov.subscriptionservice.config


import org.bagirov.subscriptionservice.exception.CustomAccessDeniedHandler
import org.bagirov.subscriptionservice.props.Role
import org.bagirov.subscriptionservice.service.JwtService
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
                    "/api/subscription/v3/api-docs/**",
                    "/api/subscription/swagger-ui/**",
                    "/api/subscription/swagger-ui.html"
                ).permitAll()
                it.requestMatchers("/api/subscription/my").hasAuthority(Role.SUBSCRIBER)
                it.requestMatchers(HttpMethod.DELETE,"/api/subscription").hasAuthority(Role.SUBSCRIBER)
                it.requestMatchers(HttpMethod.POST, "/api/subscription").hasAuthority(Role.SUBSCRIBER)
                it.requestMatchers("/api/subscription/**").hasAuthority(Role.ADMIN)
                it.anyRequest().authenticated()
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtService), BasicAuthenticationFilter::class.java)
            .exceptionHandling { exceptions ->
                exceptions.accessDeniedHandler(customAccessDeniedHandler) // Используем кастомный обработчик
            }
            .build()


}