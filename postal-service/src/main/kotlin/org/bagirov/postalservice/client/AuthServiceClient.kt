package org.bagirov.postalservice.client

import org.bagirov.postalservice.dto.response.client.AuthUserResponseClient
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@FeignClient(name = "auth-service")
interface AuthServiceClient {

    @GetMapping("/api/auth/user/details/{id}")
    fun getUserDetails(@PathVariable(name = "id") userId: UUID): AuthUserResponseClient
}