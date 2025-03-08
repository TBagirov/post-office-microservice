package org.bagirov.subscriptionservice.service


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import mu.KotlinLogging
import org.bagirov.subscriptionservice.client.PublicationServiceClient
import org.bagirov.subscriptionservice.client.SubscriberServiceClient
import org.bagirov.subscriptionservice.config.CustomUserDetails
import org.bagirov.subscriptionservice.dto.SubscriptionCreatedEvent
import org.bagirov.subscriptionservice.dto.request.SubscriptionRequest
import org.bagirov.subscriptionservice.dto.response.SubscriptionResponse
import org.bagirov.subscriptionservice.entity.SubscriptionEntity
import org.bagirov.subscriptionservice.props.SubscriptionStatus
import org.bagirov.subscriptionservice.repository.SubscriptionRepository
import org.bagirov.subscriptionservice.utill.convertToResponseDto
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriberServiceClient: SubscriberServiceClient,
    private val publicationServiceClient: PublicationServiceClient,
    private val kafkaProducerService: KafkaProducerService
) {

    private val log = KotlinLogging.logger {}

    fun getById(id: UUID): SubscriptionResponse =
        subscriptionRepository.findById(id)
            .orElseThrow { NoSuchElementException("Subscription with ID ${id} not found") }
            .convertToResponseDto()

    fun getAll(): List<SubscriptionResponse> =
        subscriptionRepository.findAll().map { it.convertToResponseDto() }

    @Transactional(readOnly = true)
    fun getSubscriptionsByUser(currentUser: CustomUserDetails): List<SubscriptionResponse> {
        val subscriber = subscriberServiceClient.getSubscriber(currentUser.getUserId())

        val subscriptions = subscriptionRepository.findBySubscriberId(subscriber.subscriberId)
            ?: throw NoSuchElementException("Subscription with Subscriber ID ${subscriber.subscriberId} not found")

        return subscriptions.map { it.convertToResponseDto() }
    }


    @Transactional
    @CircuitBreaker(name = "subscriptionService", fallbackMethod = "fallbackCreateSubscription")
    fun save(currentUser: CustomUserDetails, request: SubscriptionRequest): SubscriptionResponse {

        val tempSubscriber = subscriberServiceClient.getSubscriber(currentUser.getUserId())
        val tempPublication = publicationServiceClient.getPublication(request.publicationId)


        val subscriptionNew = SubscriptionEntity(
            subscriberId = tempSubscriber.subscriberId,
            publicationId = tempPublication.id,
            duration = request.duration,
            status =  SubscriptionStatus.PENDING_PAYMENT
        )

        val subscriptionSave: SubscriptionEntity = subscriptionRepository.save(subscriptionNew)

        // Отправляем событие в Kafka для обработки оплаты
        val event = SubscriptionCreatedEvent(
            subscriptionId = subscriptionSave.id!!,
            subscriberId = subscriptionSave.subscriberId,
            publicationId = subscriptionSave.publicationId,
            duration = subscriptionSave.duration
        )
        kafkaProducerService.sendSubscriptionCreatedEvent(event)

        return subscriptionSave.convertToResponseDto()
    }

    @Transactional
    fun updateSubscriptionStatus(subscriptionId: UUID, status: SubscriptionStatus) {
        val subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow { IllegalArgumentException("Subscription not found: $subscriptionId") }

        subscription.status = status
        subscriptionRepository.save(subscription)

        log.info("Обновлен статус подписки ${subscriptionId}: $status")
    }

    @Scheduled(fixedRate = 300_000) // Запуск каждые 5 минут (300000 мс)
    @Transactional
    fun cleanupUnpaidSubscriptions() {
        val expirationTime = LocalDateTime.now().minusMinutes(20)

        val expiredSubscriptions = subscriptionRepository.findByStatusAndStartDateBefore(
            SubscriptionStatus.PENDING_PAYMENT, expirationTime
        ) ?: return

        log.warn { "Удаление ${expiredSubscriptions.size} неоплаченных подписок..." }
        subscriptionRepository.deleteAll(expiredSubscriptions)
    }

//    @Transactional
//    @CircuitBreaker(name = "publicationService", fallbackMethod = "fallbackUpdateSubscription")
//    fun update(currentUser: CustomUserDetails, request: SubscriptionUpdateRequest): SubscriptionResponse {
//
//        // Найти подписку
//        val existingSubscription = subscriptionRepository.findById(request.id)
//            .orElseThrow { NoSuchElementException("Subscription with ID ${request.id} not found") }
//
//        val tempPublication = publicationServiceClient.getPublication(request.publicationId)
//
//        val durationUpd = Period.ofMonths(request.duration)
//
//        existingSubscription.apply {
//            publicationId = tempPublication.publicationId
//            duration = durationUpd
//        }
//
//        val subscriptionUpdate: SubscriptionEntity = subscriptionRepository.save(existingSubscription)
//
//
//        return subscriptionUpdate.convertToResponseDto()
//    }
//    // fallback метод, если PublicationService недоступен
//    fun fallbackUpdateSubscription(user: CustomUserDetails, request: SubscriberUpdateRequest, ex: Throwable): SubscriberResponse {
//        log.error("Circuit Breaker activated for publication-service! Reason: ${ex.message}", ex)
//        throw IllegalStateException("Circuit Breaker: Postal Service is currently unavailable: ${ex.message}. Please try again later.")
//    }\

    @Transactional
    fun resubscribe(currentUser: CustomUserDetails, request: SubscriptionRequest): SubscriptionResponse {
        val newSubscription = save(currentUser, request)
        delete(currentUser, request.publicationId)
        return newSubscription
    }


    @Transactional
    @CircuitBreaker(name = "subscriberService", fallbackMethod = "fallbackDeleteSubscription")
    fun delete(currentUser: CustomUserDetails, id: UUID): SubscriptionResponse {

        val existingSubscription = subscriptionRepository.findById(id)
            .orElseThrow { NoSuchElementException("Subscription with ID $id not found") }

        // Проверяем, принадлежит ли подписка текущему пользователю
        val subscriber = subscriberServiceClient.getSubscriber(currentUser.getUserId())

        if (existingSubscription.subscriberId != subscriber.subscriberId) {
            throw IllegalArgumentException("You don't have permission to delete this subscription")
        }

        // Проверяем, активна ли подписка
        if (existingSubscription.getEndDate().isAfter(LocalDateTime.now())) {
            log.warn { "Subscription $id is still active. Consider handling refund logic in PaymentService." }
        }

        // Удаляем подписку
        subscriptionRepository.delete(existingSubscription)

        return existingSubscription.convertToResponseDto()
    }


    // Фоллбек для всех сервисов
    fun fallbackCreateSubscription(
        currentUser: CustomUserDetails,
        request: SubscriptionRequest,
        ex: Throwable
    ): SubscriptionResponse {
        log.error("Circuit Breaker activated! Reason: ${ex.message}", ex)
        throw IllegalStateException("Subscription Service is currently unavailable: ${ex.message}. Please try again later.")
    }

    // Фоллбек для подписчиков
    fun fallbackDeleteSubscription(user: CustomUserDetails, id: UUID, ex: Throwable): SubscriptionResponse {
        log.error("Circuit Breaker activated for subscriber-service! Reason: ${ex.message}", ex)
        throw IllegalStateException("Circuit Breaker: Subscriber Service is currently unavailable: ${ex.message}. Please try again later.")
    }

}
