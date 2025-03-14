package org.bagirov.subscriptionservice.config


import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server


@OpenAPIDefinition(
    servers = [
        Server(url = "http://localhost:8765/api/subscription", description = "Gateway URL")
    ],
    info = Info(
        title = "subscription-service API",
        description = "Информационная система учета подписок на печатные издания",
        version = "1.0.0",
        contact = Contact(
            name = "Багиров Теймур",
            email = "t.bagirov2000@gmail.com",
            url = "https://github.com/TBagirov"
        )
    )
)
class SwaggerConfig {
}