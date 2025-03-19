package org.bagirov.reportservice.config


import org.bagirov.reportservice.exception.CustomAccessDeniedHandler
import org.bagirov.reportservice.props.Role
import org.bagirov.reportservice.service.JwtService
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
                    "/api/postal/v3/api-docs/**",
                    "/api/postal/swagger-ui/**",
                    "/api/postal/swagger-ui.html"
                ).permitAll()
                it.requestMatchers(HttpMethod.GET, "/api/postal/street/street-info").permitAll()
                it.requestMatchers(HttpMethod.GET, "/api/postal/postman/my/regions").hasAuthority(Role.POSTMAN)
                it.requestMatchers("/api/postal/**").hasAuthority(Role.ADMIN)
                it.anyRequest().authenticated()
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtService), BasicAuthenticationFilter::class.java)
            .exceptionHandling { exceptions ->
                exceptions.accessDeniedHandler(customAccessDeniedHandler) // Используем кастомный обработчик
            }
            .build()


}