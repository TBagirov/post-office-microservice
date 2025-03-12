package org.bagirov.subscriptionservice.service


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import mu.KotlinLogging
import org.bagirov.subscriptionservice.client.AuthServiceClient
import org.bagirov.subscriptionservice.client.PublicationServiceClient
import org.bagirov.subscriptionservice.client.SubscriberServiceUserClient
import org.bagirov.subscriptionservice.config.CustomUserDetails
import org.bagirov.subscriptionservice.dto.SubscriptionCancelledEvent
import org.bagirov.subscriptionservice.dto.SubscriptionConfirmedEvent
import org.bagirov.subscriptionservice.dto.SubscriptionCreatedEvent
import org.bagirov.subscriptionservice.dto.SubscriptionExpiredEvent
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
    private val subscriberServiceUserClient: SubscriberServiceUserClient,
    private val publicationServiceClient: PublicationServiceClient,
    private val authServiceClient: AuthServiceClient,
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
        val subscriber = subscriberServiceUserClient.getSubscriberByUserId(currentUser.getUserId())

        val subscriptions = subscriptionRepository.findBySubscriberId(subscriber.subscriberId)
            ?: throw NoSuchElementException("Subscription with Subscriber ID ${subscriber.subscriberId} not found")

        return subscriptions.map { it.convertToResponseDto() }
    }


    @Transactional
    @CircuitBreaker(name = "subscriptionService", fallbackMethod = "fallbackCreateSubscription")
    fun save(currentUser: CustomUserDetails, request: SubscriptionRequest): SubscriptionResponse {

        val tempSubscriber = subscriberServiceUserClient.getSubscriberByUserId(currentUser.getUserId())
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

        val subscriber = subscriberServiceUserClient.getSubscriber(subscription.subscriberId)
        val publication = publicationServiceClient.getPublication(subscription.publicationId)

        // **Получаем email и username из AuthService**
        val userDetails = authServiceClient.getUserDetails(subscriber.userId)

        when (status) {
            SubscriptionStatus.ACTIVE -> {
                val event = SubscriptionConfirmedEvent(
                    email = userDetails.email,
                    username = userDetails.username,
                    publicationName = publication.title,
                    startDate = subscription.startDate.toString(),
                    duration = subscription.duration
                )
                kafkaProducerService.sendNotificationEvent(event)
            }
            SubscriptionStatus.CANCELLED -> {
                val event = SubscriptionCancelledEvent(
                    email = userDetails.email,
                    publicationName = publication.title,
                    cancellationReason = "Платеж не прошел"
                )
                kafkaProducerService.sendNotificationEvent(event)
            }
            SubscriptionStatus.EXPIRED -> {
                val event = SubscriptionExpiredEvent(
                    email = userDetails.email,
                    publicationName = publication.title,
                    expirationDate = LocalDateTime.now().toString()
                )
                kafkaProducerService.sendNotificationEvent(event)
            }
            else -> log.info("Нет события для статуса: $status")
        }
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
        val subscriber = subscriberServiceUserClient.getSubscriberByUserId(currentUser.getUserId())

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
