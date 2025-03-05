package org.bagirov.subscriberservice.config


import org.bagirov.authservice.props.Role
import org.bagirov.subscriberservice.service.JwtService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtService: JwtService
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity) =
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.PUT, "/api/subscriber/update").hasAuthority(Role.SUBSCRIBER)
                it.requestMatchers(HttpMethod.GET, "/api/subscriber/**").hasAnyAuthority(Role.SUBSCRIBER, Role.ADMIN)
                it.anyRequest().authenticated()
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtService), BasicAuthenticationFilter::class.java)
            .build()


}