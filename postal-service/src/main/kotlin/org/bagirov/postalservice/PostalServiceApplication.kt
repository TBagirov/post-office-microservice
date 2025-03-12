package org.bagirov.postalservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
class PostalServiceApplication

fun main(args: Array<String>) {
    runApplication<PostalServiceApplication>(*args)
}
