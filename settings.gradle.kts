plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "post-office-microservice"

include("eureka-server")
include("gateway-api")
include("auth-service")
