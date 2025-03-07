package org.bagirov.authservice.service

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.bagirov.authservice.entity.UserEntity
import org.bagirov.authservice.props.JwtProperties
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
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
    private val userDetailsService: UserDetailsService,
) {
    var key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret))

    fun createAccessToken(
        user: UserEntity
    ): String {
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
        val validity = Instant.now()
            .plus(jwtProperties.refreshExpiration, ChronoUnit.DAYS)
        return Jwts.builder()
            .subject(user.username)
            .claim("id", user.id.toString())
            .expiration(Date.from(validity))
            .signWith(key)
            .compact()
    }

//    fun refreshUserTokens(
//        refreshToken: String?
//    ): AuthenticationResponse {
//
//        if (!isValid(refreshToken)) {
//            throw AccessDeniedException("не валидный токен")
//        }
//        val userId = getId(refreshToken)
//        val user: UserEntity? = userService.getById(UUID.fromString(userId))
//
//        val authenticationResponse: AuthenticationResponse = AuthenticationResponse(
//            id = UUID.fromString(userId),
//            username = user!!.username,
//            accessToken = createAccessToken(user),
//            refreshToken = createRefreshToken(user),
//        )
//
//        return authenticationResponse
//    }


//    fun isValid(
//        token: String?,
//        userDetails: UserDetails
//    ): Boolean {
//        val username = getUsername(token!!)
//        val claims = Jwts
//            .parser()
//            .verifyWith(key)
//            .build()
//            .parseSignedClaims(token)
//        return claims.payload
//            .expiration
//            .after(Date()) && username == userDetails.username
//    }

    fun extractClaims(token: String): Claims {
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        } catch (ex: ExpiredJwtException) {
            throw ex // Пробрасываем дальше, чтобы обработать в GlobalExceptionHandler
        } catch (ex: MalformedJwtException) {
            throw JwtException("Malformed JWT token", ex)
        } catch (ex: JwtException) {
            throw JwtException("Invalid JWT token", ex)
        }
    }

    fun getId(token: String): String =
        extractClaims(token).get("id", String::class.java)

    fun getUsername(token: String): String =
        extractClaims(token).subject

    fun isValid(token: String?, userDetails: UserDetails): Boolean {
        if (token == null) return false
        val claims = extractClaims(token)
        return claims.expiration.after(Date()) && claims.subject == userDetails.username
    }

    fun isValidExpired(token: String): Boolean {
        val claims = extractClaims(token)
        return claims.expiration.after(Date())
    }

    fun getAuthentication(
        token: String
    ): Authentication {
        val username = getUsername(token)
        val userDetails = userDetailsService.loadUserByUsername(
            username
        )
        return UsernamePasswordAuthenticationToken(
            userDetails,
            "",
            userDetails.authorities
        )
    }


}