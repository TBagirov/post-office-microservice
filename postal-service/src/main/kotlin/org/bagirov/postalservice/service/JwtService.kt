package org.bagirov.postalservice.service

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import mu.KotlinLogging
import org.bagirov.postalservice.props.JwtProperties
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {

    private val log = KotlinLogging.logger {}

    private var key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret))

    fun extractClaims(token: String): Claims {
        return try {
            log.info { "Extracting claims from JWT token..." }
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        } catch (ex: ExpiredJwtException) {
            log.error { "JWT token has expired: ${ex.message}" }
            throw ex
        } catch (ex: MalformedJwtException) {
            log.error { "Malformed JWT token: ${ex.message}" }
            throw JwtException("Malformed JWT token", ex)
        } catch (ex: JwtException) {
            log.error { "Invalid JWT token: ${ex.message}" }
            throw JwtException("Invalid JWT token", ex)
        }
    }

    fun getId(token: String): String {
        val id = extractClaims(token).get("id", String::class.java)
        log.info { "Extracted user ID from JWT token: $id" }
        return id
    }

    fun getUsername(token: String): String {
        val username = extractClaims(token).subject
        log.info { "Extracted username from JWT token: $username" }
        return username
    }

    fun getUserRole(token: String): String {
        val role = extractClaims(token).get("role", String::class.java)
        log.info { "Extracted user role from JWT token: $role" }
        return role
    }

    fun isValid(token: String): Boolean {
        val claims = extractClaims(token)
        val isValid = claims.expiration.after(Date())
        log.info { "JWT token validation result: $isValid" }
        return isValid
    }


}
