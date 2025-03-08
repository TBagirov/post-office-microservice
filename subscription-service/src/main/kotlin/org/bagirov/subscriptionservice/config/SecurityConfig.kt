package org.bagirov.subscriptionservice.config


import org.bagirov.subscriptionservice.props.Role
import org.bagirov.subscriptionservice.exception.CustomAccessDeniedHandler
import org.bagirov.subscriptionservice.service.JwtService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtService: JwtService,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity) =
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.GET, "/api/subscription").hasAuthority(Role.ADMIN)
                it.requestMatchers(HttpMethod.GET, "/api/subscription/**").hasAuthority(Role.SUBSCRIBER)
                it.anyRequest().authenticated()
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtService), BasicAuthenticationFilter::class.java)
            .exceptionHandling { exceptions ->
                exceptions.accessDeniedHandler(customAccessDeniedHandler) // Используем кастомный обработчик
            }
            .build()


}