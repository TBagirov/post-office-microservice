package org.bagirov.authservice.controller

import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import org.bagirov.authservice.dto.response.UserResponse
import org.bagirov.authservice.service.UserService
import org.springframework.http.ResponseEntity
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
    fun getSubscriber(@PathVariable id: UUID): ResponseEntity<UserResponse> {
        log.info { "Request Subscriber by id: $id" }
        return ResponseEntity.ok(userService.getById(id))
    }

    @GetMapping()
    @Operation(
        summary = "Получение всех пользователей",
        description = "Получение данных всех пользователей"
    )
    fun getAll(): ResponseEntity<List<UserResponse>> =
        ResponseEntity.ok(userService.getAll())

    @DeleteMapping("/delete")
    @Operation(
        summary = "Удаление пользователя по id",
        description = "Удаление пользователя по id")
    fun delete(@RequestParam("id") id: UUID):
            ResponseEntity<UserResponse>
    {
        log.info { "Request delete Subscriber" }
        return ResponseEntity.ok(userService.delete(id))
    }

}