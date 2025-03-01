package org.bagirov.authservice.config

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.bagirov.authservice.service.JwtService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
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

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader = request.getHeader("Authorization")

            if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response)
                return
            }

            val jwt = authHeader.substring(7)

            val userUsername = jwtService.getUsername(jwt)

            if (userUsername != null && SecurityContextHolder.getContext().authentication == null) {
                val userDetails = this.userDetailsService.loadUserByUsername(userUsername)

                if (jwtService.isValid(jwt, userDetails)) {
//                    val authToken = UsernamePasswordAuthenticationToken(userUsername, jwt, userDetails.authorities)
                    val authToken = UsernamePasswordAuthenticationToken(userDetails, jwt, userDetails.authorities)
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
//                    println("AUTH: User - $userUsername, Roles - ${userDetails.authorities}")
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }

            filterChain.doFilter(request, response)

        } catch (ex: ExpiredJwtException) {
            handleException(response, "The token has expired", HttpServletResponse.SC_UNAUTHORIZED)
        } catch (ex: JwtException) {
            handleException(response, "Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED)
        } catch (ex: AuthenticationException) {
            handleException(response, "Authentication failed", HttpServletResponse.SC_UNAUTHORIZED)
        } catch (ex: Exception) {
            handleException(response, "Unexpected Error", HttpServletResponse.SC_UNAUTHORIZED)
        }
    }

    private fun handleException(response: HttpServletResponse, message: String, status: Int) {
        response.status = status
        response.contentType = "application/json"
        response.writer.write("""{"error": "$message"}""")
    }
}