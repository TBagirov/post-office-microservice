package org.bagirov.subscriberservice.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.bagirov.subscriberservice.config.CustomUserDetails
import org.bagirov.subscriberservice.dto.request.SubscriberUpdateRequest
import org.bagirov.subscriberservice.dto.response.SubscriberResponse
import org.bagirov.subscriberservice.dto.response.client.SubscriberResponseUserClient
import org.bagirov.subscriberservice.service.SubscriberService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*


@CrossOrigin(origins = arrayOf("http://localhost:3000"))
@RestController
@RequestMapping("/api/subscriber")
@Tag(name = "SubscriberController", description = "Контроллер для взаимодействия с подписчиками на печатные издания")
class SubscriberController(
    val subscriberService: SubscriberService
) {
    private val log = KotlinLogging.logger {}

    @GetMapping("/{id}")
    @Operation(
        summary = "Получение подписчика по id",
        description = "Получение данных о подписчике по id"
    )
    fun getSubscriber(@PathVariable id: UUID): ResponseEntity<SubscriberResponse> {
        log.info { "Request Subscriber by id: $id" }
        return ResponseEntity.ok(subscriberService.getById(id))
    }

    @GetMapping()
    @Operation(
        summary = "Получение всех подписчиков",
        description = "Получение данных всех подписчиков"
    )
    fun getAll(@Parameter(hidden = true) @AuthenticationPrincipal user: CustomUserDetails):
            ResponseEntity<List<SubscriberResponse>>
    {
        log.info { "Request all Subscriber ${user.getUserId()}" }
        return ResponseEntity.ok(subscriberService.getAll())
    }


    @PutMapping("/update")
    @Operation(
        summary = "Редактирование подписчика по id",
        description = "Редактирование данных подписчика по id"
    )
    fun update(@Parameter(hidden = true) @AuthenticationPrincipal user: CustomUserDetails,
               @RequestBody subscriber: SubscriberUpdateRequest):
            ResponseEntity<SubscriberResponse>
    {
        log.info { "Request to update Subscriber" }
        return ResponseEntity.ok(subscriberService.update(user, subscriber))
    }




    @Value("\${internal.api-secret}")
    private lateinit var apiSecret: String

    @GetMapping("/client/user/{id}")
    fun getSubscriberByUserId(
        @Parameter(hidden = true) @RequestHeader(value = "X-Internal-Call", required = false) secret: String?,
        @PathVariable(name = "id") userId: UUID
    ): ResponseEntity<SubscriberResponseUserClient> {
        if (secret != apiSecret) {
            log.warn { "Forbidden access to /user/$userId. Invalid secret: $secret" }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        log.info { "Request Subscriber by id: $userId" }
        return ResponseEntity.ok(subscriberService.getByUserId(userId))
    }

    @GetMapping("/client/{id}")
    fun getSubscriberById(
        @Parameter(hidden = true) @RequestHeader(value = "X-Internal-Call", required = false) secret: String?,
        @PathVariable(name = "id") subscriberId: UUID
    ): ResponseEntity<SubscriberResponse> {
        if (secret != apiSecret) {
            log.warn { "Forbidden access to /client/$subscriberId. Invalid secret: $secret" }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        log.info { "Request Subscriber by id: $subscriberId" }
        return ResponseEntity.ok((subscriberService.getById(subscriberId)))
    }
//
//    @DeleteMapping("/delete")
//    @Operation(
//        summary = "Удаление подписчика по id",
//        description = "Удаление подписчика по id, " +
//                "удаленный подписчик в записях других таблиц изменится на null"
//    )
//    fun delete(@AuthenticationPrincipal user: UserEntity): ResponseEntity<SubscriberResponse> {
//        log.info { "Request delete Subscriber" }
//        return ResponseEntity.ok(subscriberService.delete(user))
//    }
}