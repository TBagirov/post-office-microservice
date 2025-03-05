package org.bagirov.subscriberservice.service

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import mu.KotlinLogging
import org.bagirov.subscriberservice.client.PostalServiceClient
import org.bagirov.subscriberservice.config.CustomUserDetails
import org.bagirov.subscriberservice.dto.request.SubscriberUpdateRequest
import org.bagirov.subscriberservice.dto.response.SubscriberResponse
import org.bagirov.subscriberservice.repository.SubscriberRepository
import org.bagirov.subscriberservice.utill.convertToResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class SubscriberService(
    private val subscriberRepository: SubscriberRepository,
    private val postalServiceClient: PostalServiceClient
) {

    private val log = KotlinLogging.logger {}

    fun getById(id: UUID): SubscriberResponse =
        subscriberRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Subscriber with ID ${id} not found") }
            .convertToResponseDto()

    fun getAll(): List<SubscriberResponse> =
        subscriberRepository.findAll().map { it.convertToResponseDto() }



    @Transactional
    @CircuitBreaker(name = "postalService", fallbackMethod = "fallbackUpdateSubscriber")
    fun update(currentUser: CustomUserDetails, subscriberRequest: SubscriberUpdateRequest): SubscriberResponse {
        log.info { "Updating subscriber for user: $currentUser with request: $subscriberRequest" }

        val subscriber = subscriberRepository.findByUserId(currentUser.getUserId())
            ?: throw NoSuchElementException("Subscriber not found for user ID ${currentUser.getUserId()}")


        // Запрос в `PostalService` для получения `streetId` и `districtId`
        val streetDistrict = postalServiceClient.getStreetAndDistrict(subscriberRequest.streetName)

        // Обновляем подписчика
        subscriber.apply {
            streetId = streetDistrict.streetId
            districtId = streetDistrict.districtId
            subAddress = subscriberRequest.subAddress
            building = subscriberRequest.building
        }

        val updatedSubscriber = subscriberRepository.save(subscriber)

        return updatedSubscriber.convertToResponseDto()
    }

    // fallback метод, если PostalService недоступен
    fun fallbackUpdateSubscriber(user: CustomUserDetails, request: SubscriberUpdateRequest, ex: Throwable): SubscriberResponse {
        log.error("Circuit Breaker activated for postal-service! Reason: ${ex.message}", ex)
        throw IllegalStateException("Circuit Breaker: Postal Service is currently unavailable: ${ex.message}. Please try again later.")
    }

//
//
//    @Transactional
//    fun delete(currentUser: UserEntity): SubscriberResponse {
//        val user = userRepository.findById(currentUser.id!!)
//            .orElseThrow { IllegalArgumentException("Subscriber with ID ${currentUser.id} not found") }
//
//        // Найти существующего подписчика
//        val existingSubscriber = user.subscriber
//            ?: throw NoSuchElementException("Subscriber with ID ${currentUser.id} not found")
//
//        // Удалить подписчика
//        userRepository.delete(user)
//
//        return existingSubscriber.convertToResponseDto()
//    }


}
