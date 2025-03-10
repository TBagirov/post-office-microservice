package org.bagirov.postalservice.service

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.bagirov.postalservice.props.JwtProperties
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {

    var key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret))

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

    fun getUserRole(token: String): String =
        extractClaims(token).get("role", String::class.java)

    fun isValid(token: String): Boolean {
        val claims = extractClaims(token)
        return claims.expiration.after(Date())
    }



}
