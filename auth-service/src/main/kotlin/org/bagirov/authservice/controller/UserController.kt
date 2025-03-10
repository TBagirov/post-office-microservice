package org.bagirov.authservice.controller

import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import org.bagirov.authservice.dto.request.UserUpdateRequest
import org.bagirov.authservice.dto.response.UserResponse
import org.bagirov.authservice.dto.response.client.AuthUserResponseClient
import org.bagirov.authservice.entity.UserEntity
import org.bagirov.authservice.service.UserService
import org.bagirov.authservice.utill.convertToResponseClientDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/auth/user")
class UserController(
    private val userService: UserService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping("/{id}")
    @Operation(
        summary = "Получение пользователя по id",
        description = "Получение данных о пользователе по id"
    )
    fun getUser(@PathVariable id: UUID): ResponseEntity<UserResponse> {
        log.info { "Request User by id: $id" }
        return ResponseEntity.ok(userService.getById(id))
    }

    @Value("\${internal.api-secret}")
    private lateinit var apiSecret: String

    @GetMapping("/details/{id}")
    fun getSubscriberByUserId(
        @RequestHeader(value = "X-Internal-Call", required = false) secret: String?,
        @PathVariable(name = "id") userId: UUID
    ): ResponseEntity<AuthUserResponseClient> {
        if (secret != apiSecret) {
            log.warn { "Forbidden access to /user/$userId. Invalid secret: $secret" }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        log.info { "Request Subscriber by id: $userId" }
        return ResponseEntity.ok(userService.getById(userId).convertToResponseClientDto())
    }

    @GetMapping()
    @Operation(
        summary = "Получение всех пользователей",
        description = "Получение данных всех пользователей"
    )
    fun getAll(): ResponseEntity<List<UserResponse>> =
        ResponseEntity.ok(userService.getAll())

    @PutMapping("/update")
    fun update(@AuthenticationPrincipal user: UserEntity,
               @RequestBody request: UserUpdateRequest):
            ResponseEntity<UserResponse>
    {
        log.info {"Request update User"}
        return ResponseEntity.ok(userService.update(user, request))
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "Удаление пользователя по id",
        description = "Удаление пользователя по id"
    )
    fun delete(@RequestParam("id") id: UUID):
            ResponseEntity<UserResponse>
    {
        log.info { "Request delete User" }
        return ResponseEntity.ok(userService.delete(id))
    }

}