package org.bagirov.subscriptionservice.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.bagirov.subscriptionservice.config.CustomUserDetails
import org.bagirov.subscriptionservice.dto.request.SubscriptionRequest
import org.bagirov.subscriptionservice.dto.response.SubscriptionResponse
import org.bagirov.subscriptionservice.service.SubscriptionService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@CrossOrigin(origins = arrayOf("http://localhost:3000"))
@RestController
@RequestMapping("/api/subscription")
@Tag(name = "SubscriptionController", description = "Контроллер для взаимодействия с подписками подписчиков на печатные издания")
class SubscriptionController(
    private val subscriptionService: SubscriptionService
) {
    private val log = KotlinLogging.logger {}

    @GetMapping("/{id}")
    @Operation(
        summary = "Получение подписки по id",
        description = "Получение данных подписки по id"
    )
    fun getSubscription(@PathVariable id: UUID):ResponseEntity<SubscriptionResponse>{
        log.info {"Request get Subscription by id: $id"}
        return ResponseEntity.ok(subscriptionService.getById(id))
    }

    @GetMapping()
    @Operation(
        summary = "Получение всех подписок",
        description = "Получение данных о всех подписках"
    )
    fun getAll(): ResponseEntity<List<SubscriptionResponse>> {
        log.info { "Request get all Subscription" }
        return ResponseEntity.ok(subscriptionService.getAll())
    }

    @PostMapping()
    @Operation(
        summary = "Добавление подписки",
        description = "Добавление данных о подписке"
    )
    fun save(@AuthenticationPrincipal user: CustomUserDetails,
             @RequestBody subscription: SubscriptionRequest):
            ResponseEntity<SubscriptionResponse>
    {
        log.info { "Request create Subscription" }
        return ResponseEntity.ok(subscriptionService.save(user, subscription))
    }

//    @PutMapping()
//    @Operation(
//        summary = "Редактирование подписки по id",
//        description = "Редактирование данных подписки по id"
//    )
//    fun update(@AuthenticationPrincipal user: CustomUserDetails,
//               @RequestBody subscription: SubscriptionUpdateRequest): ResponseEntity<SubscriptionResponse> {
//        log.info { "Request update Subscription by id: ${subscription.id}" }
//        return ResponseEntity.ok(subscriptionService.update(user, subscription))
//    }

    @GetMapping("/my")
    @Operation(summary = "Получение подписок текущего пользователя")
    fun getUserSubscriptions(@AuthenticationPrincipal user: CustomUserDetails):
            ResponseEntity<List<SubscriptionResponse>>
    {
        log.info { "Fetching subscriptions for user: ${user.getUserId()}" }
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByUser(user))
    }


    @DeleteMapping()
    @Operation(
        summary = "Удаление подписки по id",
        description = "Удаление подписки по id, " +
                "удаленная подписка в записях других таблиц изменится на null"
    )
    fun delete(@AuthenticationPrincipal user: CustomUserDetails, @RequestParam id: UUID):
            ResponseEntity<SubscriptionResponse>
    {
        log.info { "Request delete Subscription by id: $id" }
        return ResponseEntity.ok(subscriptionService.delete(user, id))
    }

}