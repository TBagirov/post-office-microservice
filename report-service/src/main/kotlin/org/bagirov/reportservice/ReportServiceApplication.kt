package org.bagirov.reportservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
class ReportServiceApplication

fun main(args: Array<String>) {
    runApplication<ReportServiceApplication>(*args)
}
