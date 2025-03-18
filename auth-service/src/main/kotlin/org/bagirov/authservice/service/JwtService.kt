package org.bagirov.authservice.service

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import mu.KotlinLogging
import org.bagirov.authservice.entity.UserEntity
import org.bagirov.authservice.props.JwtProperties
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey


@Service
class JwtService(
    private val jwtProperties: JwtProperties,
    private val userDetailsService: UserDetailsService
) {
    private var key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret))

    private val log = KotlinLogging.logger {}

    fun createAccessToken(user: UserEntity): String {
        log.info { "Generating access token for user: ${user.username}" }
        val validity = Instant.now()
            .plus(jwtProperties.accessExpiration, ChronoUnit.HOURS)
        return Jwts.builder()
            .subject(user.username)
            .claim("id", user.id.toString())
            .claim("role", user.role.name)
            .expiration(Date.from(validity))
            .signWith(key)
            .compact()
    }

    fun createRefreshToken(user: UserEntity): String {
        log.info { "Generating refresh token for user: ${user.username}" }
        val validity = Instant.now()
            .plus(jwtProperties.refreshExpiration, ChronoUnit.DAYS)
        return Jwts.builder()
            .subject(user.username)
            .claim("id", user.id.toString())
            .expiration(Date.from(validity))
            .signWith(key)
            .compact()
    }

    fun extractClaims(token: String): Claims {
        return try {
            log.info { "Extracting claims from token" }
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        } catch (ex: ExpiredJwtException) {
            log.warn { "Token has expired" }
            throw ex
        } catch (ex: MalformedJwtException) {
            log.error { "Malformed JWT token" }
            throw JwtException("Malformed JWT token", ex)
        } catch (ex: JwtException) {
            log.error { "Invalid JWT token" }
            throw JwtException("Invalid JWT token", ex)
        }
    }

    fun getId(token: String): String {
        log.info { "Extracting user ID from token" }
        return extractClaims(token).get("id", String::class.java)
    }

    fun getUsername(token: String): String {
        log.info { "Extracting username from token" }
        return extractClaims(token).subject
    }

    fun isValid(token: String?, userDetails: UserDetails): Boolean {
        if (token == null) return false
        val claims = extractClaims(token)
        log.info { "Validating token for user: ${userDetails.username}" }
        return claims.expiration.after(Date()) && claims.subject == userDetails.username
    }

    fun isValidExpired(token: String): Boolean {
        log.info { "Checking if token is expired" }
        val claims = extractClaims(token)
        return claims.expiration.after(Date())
    }

}