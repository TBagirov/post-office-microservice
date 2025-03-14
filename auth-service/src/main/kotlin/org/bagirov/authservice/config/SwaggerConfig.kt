package org.bagirov.authservice.config


import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    servers = [
        Server(url = "http://localhost:8765/api/auth", description = "Gateway URL")
    ],
    info = Info(
        title = "auth-service API",
        description = "Информационная система учета подписок на печатные издания",
        version = "1.0.0",
        contact = Contact(
            name = "Багиров Теймур",
            email = "t.bagirov2000@gmail.com",
            url = "https://github.com/TBagirov"
        )
    )
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