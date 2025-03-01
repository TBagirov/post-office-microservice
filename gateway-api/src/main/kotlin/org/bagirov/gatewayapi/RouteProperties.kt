package org.bagirov.gatewayapi

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "gateway")
class RouteProperties {
    var routes: List<Route> = listOf()

    data class Route (
        var id: String,
        var path: String,
        var uri: String
    )
}