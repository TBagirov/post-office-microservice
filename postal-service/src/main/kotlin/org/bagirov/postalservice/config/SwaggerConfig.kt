package org.bagirov.postalservice.config


import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import io.swagger.v3.oas.annotations.servers.Server


@OpenAPIDefinition(
    servers = [
        Server(url = "http://localhost:8765", description = "Gateway URL")
    ],
    info = Info(
        title = "postal-service API",
        description = "Информационная система учета подписок на печатные издания",
        version = "1.0.0",
        contact = Contact(
            name = "Багиров Теймур",
            email = "t.bagirov2000@gmail.com",
            url = "https://github.com/TBagirov"
        )
    ),
    security = [SecurityRequirement(name = "bearerAuth")]
)
@SecuritySchemes(
    SecurityScheme(
        name = "bearerAuth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT"
    )
)
class SwaggerConfig {
}