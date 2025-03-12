package org.bagirov.subscriberservice.config

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.bagirov.subscriberservice.service.JwtService
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
        log.info("JwtAuthenticationFilter executing for ${request.requestURI}")

        try {
            val authHeader = request.getHeader("Authorization")

            if (authHeader.isNullOrEmpty() || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response)
                return
            }

            val jwt = authHeader.substring(7)

            if (!jwtService.isValid(jwt)) {
                throw JwtException("Invalid JWT token")
            }

            val role = jwtService.getUserRole(jwt)
            val userId = UUID.fromString(jwtService.getId(jwt))

            if (SecurityContextHolder.getContext().authentication == null) {
                val authorities = listOf(SimpleGrantedAuthority(role))

                val userDetails = CustomUserDetails(userId, authorities) // Создаем объект UserDetails

                val authToken = UsernamePasswordAuthenticationToken(userDetails, null, authorities)
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authToken
            }

            filterChain.doFilter(request, response)

        } catch (ex: ExpiredJwtException) {
            handleException(response, "The token has expired", HttpServletResponse.SC_UNAUTHORIZED)
        } catch (ex: JwtException) {
            handleException(response, "Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED)
        } catch (ex: Exception) {
            handleException(response, "Unexpected Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        }
    }

    private fun handleException(response: HttpServletResponse, message: String, status: Int) {
        response.status = status
        response.contentType = "application/json"
        response.writer.write("""{"error": "$message"}""")
    }
}
