package org.bagirov.publicationservice.config


import org.bagirov.publicationservice.exception.CustomAccessDeniedHandler
import org.bagirov.publicationservice.props.Role
import org.bagirov.publicationservice.service.JwtService
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
                    "/api/publication/v3/api-docs/**",
                    "/api/publication/swagger-ui/**",
                    "/api/publication/swagger-ui.html"
                ).permitAll()
                it.requestMatchers("api/publication/user/**").permitAll()
                it.requestMatchers(HttpMethod.GET,  "/api/publication").permitAll()
                it.requestMatchers( "/api/publication/**").hasAuthority(Role.ADMIN)
                it.anyRequest().authenticated()
            }
            .addFilterBefore(JwtAuthenticationFilter(jwtService), BasicAuthenticationFilter::class.java)
            .exceptionHandling { exceptions ->
                exceptions.accessDeniedHandler(customAccessDeniedHandler) // Используем кастомный обработчик
            }
            .build()



}