package org.bagirov.authservice.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "security.jwt")
class JwtProperties(
    var secret: String = "",
    var accessExpiration: Long = 0,
    var refreshExpiration: Long = 0
)