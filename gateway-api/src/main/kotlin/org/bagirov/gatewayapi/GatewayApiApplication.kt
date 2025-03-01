package org.bagirov.gatewayapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GatewayApiApplication

fun main(args: Array<String>) {
    runApplication<GatewayApiApplication>(*args)
}
