package org.bagirov.authservice.config

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.bagirov.authservice.service.JwtService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    private val log = KotlinLogging.logger {}

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader = request.getHeader("Authorization")

            if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer ")) {
                log.debug { "No Authorization header found, skipping filter for request: ${request.requestURI}" }
                filterChain.doFilter(request, response)
                return
            }

            val jwt = authHeader.substring(7)
            log.info { "Processing JWT authentication for request: ${request.requestURI}" }

            val userUsername = jwtService.getUsername(jwt)

            if (userUsername != null && SecurityContextHolder.getContext().authentication == null) {
                val userDetails = this.userDetailsService.loadUserByUsername(userUsername)

                if (jwtService.isValid(jwt, userDetails)) {
                    log.info { "JWT authentication successful for user: $userUsername" }
                    val authToken = UsernamePasswordAuthenticationToken(userDetails, jwt, userDetails.authorities)
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                } else {
                    log.warn { "JWT authentication failed for user: $userUsername" }
                }
            }

            filterChain.doFilter(request, response)
        } catch (ex: ExpiredJwtException) {
            log.warn { "JWT token has expired" }
            handleException(response, "The token has expired", HttpServletResponse.SC_UNAUTHORIZED)
        } catch (ex: JwtException) {
            log.error { "Invalid JWT token: ${ex.message}" }
            handleException(response, "Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED)
        } catch (ex: Exception) {
            log.error { "Unexpected error during JWT authentication: ${ex.message}" }
            handleException(response, "Unexpected Error", HttpServletResponse.SC_UNAUTHORIZED)
        }
    }

    private fun handleException(response: HttpServletResponse, message: String, status: Int) {
        response.status = status
        response.contentType = "application/json"
        response.writer.write("""{"error": "$message"}""")
    }
}