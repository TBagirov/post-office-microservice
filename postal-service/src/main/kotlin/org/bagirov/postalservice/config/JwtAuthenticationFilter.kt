package org.bagirov.postalservice.config

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.bagirov.postalservice.service.JwtService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    private val log = KotlinLogging.logger {}

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.info { "Executing JwtAuthenticationFilter for request: ${request.requestURI}" }

        try {
            val authHeader = request.getHeader("Authorization")

            if (authHeader.isNullOrEmpty() || !authHeader.startsWith("Bearer ")) {
                log.warn { "No valid Authorization header found, proceeding with filter chain" }
                filterChain.doFilter(request, response)
                return
            }

            val jwt = authHeader.substring(7)

            if (!jwtService.isValid(jwt)) {
                log.error { "Invalid JWT token provided" }
                throw JwtException("Invalid JWT token")
            }

            val role = jwtService.getUserRole(jwt)
            val userId = UUID.fromString(jwtService.getId(jwt))

            if (SecurityContextHolder.getContext().authentication == null) {
                val authorities = listOf(SimpleGrantedAuthority(role))

                val userDetails = CustomUserDetails(userId, authorities)

                val authToken = UsernamePasswordAuthenticationToken(userDetails, null, authorities)
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authToken
                log.info { "JWT authentication successful for user ID: $userId" }
            }

            filterChain.doFilter(request, response)

        } catch (ex: ExpiredJwtException) {
            log.error { "JWT token has expired: ${ex.message}" }
            handleException(response, "The token has expired", HttpServletResponse.SC_UNAUTHORIZED)
        } catch (ex: JwtException) {
            log.error { "Invalid JWT token: ${ex.message}" }
            handleException(response, "Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED)
        } catch (ex: Exception) {
            log.error(ex) { "Unexpected error in JwtAuthenticationFilter: ${ex.message}" }
            handleException(response, "Unexpected Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        }
    }

    private fun handleException(response: HttpServletResponse, message: String, status: Int) {
        response.status = status
        response.contentType = "application/json"
        response.writer.write("""{"error": "$message"}""")
    }
}
