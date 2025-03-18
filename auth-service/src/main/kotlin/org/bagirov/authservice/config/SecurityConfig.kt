package org.bagirov.authservice.config

import org.bagirov.authservice.exception.CustomAccessDeniedHandler
import org.bagirov.authservice.props.Role
import org.bagirov.authservice.service.JwtService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { obj: AbstractHttpConfigurer<*, *> -> obj.disable() }
            .addFilterBefore(JwtAuthenticationFilter(userDetailsService, jwtService), UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry
                    .requestMatchers(
                        "/api/auth/v3/api-docs/**",
                        "/api/auth/swagger-ui/**",
                        "/api/auth/swagger-ui.html"
                    ).permitAll()
                    .requestMatchers("api/auth/registration-postman").hasAuthority(Role.ADMIN)
                    .requestMatchers("api/auth/become-subscriber").hasAuthority(Role.GUEST)
                    .requestMatchers("/api/auth/user/details/**").permitAll()
                    .requestMatchers("/api/auth/user/update").hasAnyAuthority(Role.SUBSCRIBER, Role.POSTMAN)
                    .requestMatchers("/api/auth/user/**").hasAuthority(Role.ADMIN)
                    .requestMatchers("/api/auth/role/**").hasAuthority(Role.ADMIN)
                    .requestMatchers(
                        "/api/auth/**",
                    )
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .httpBasic(Customizer.withDefaults())
            .sessionManagement { httpSecuritySessionManagementConfigurer ->
                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }.exceptionHandling { exceptions ->
                exceptions.accessDeniedHandler(customAccessDeniedHandler) // Используем кастомный обработчик
            }

        return http.build()
    }
}