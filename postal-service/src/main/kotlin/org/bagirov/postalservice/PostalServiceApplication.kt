package org.bagirov.postalservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class PostalServiceApplication

fun main(args: Array<String>) {
    runApplication<PostalServiceApplication>(*args)
}
