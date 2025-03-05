package org.bagirov.subscriberservice.service

import org.bagirov.subscriberservice.dto.response.SubscriberResponse
import org.bagirov.subscriberservice.repository.SubscriberRepository
import org.bagirov.subscriberservice.utill.convertToResponseDto
import org.springframework.stereotype.Service
import java.util.*

@Service
class SubscriberService(
    private val subscriberRepository: SubscriberRepository
) {

    fun getById(id: UUID): SubscriberResponse =
        subscriberRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Subscriber with ID ${id} not found") }
            .convertToResponseDto()

    fun getAll(): List<SubscriberResponse> =
        subscriberRepository.findAll().map { it.convertToResponseDto() }

//
//
//    @Transactional
//    fun update(currentUser: UserEntity, subscriberRequest: SubscriberUpdateRequest): SubscriberResponse {
//
//        val user = userRepository.findById(currentUser.id!!)
//            .orElseThrow { NoSuchElementException("Запрос от несуществующего пользователя") }
//
//        val subscriber = user.subscriber
//            ?: throw NoSuchElementException("Subscriber profile not found for user with ID ${currentUser.id}")
//
//        val tempStreet: StreetEntity = streetRepository
//            .findById(subscriberRequest.streetId).orElse(null)
//
//        val tempDistrict: DistrictEntity = districtRepository
//            .findById(subscriberRequest.districtId).orElse(null)
//
//        user.subscriber!!.street = tempStreet
//        user.subscriber!!.district = tempDistrict
//        user.subscriber!!.subAddress = subscriberRequest.subAddress
//        user.subscriber!!.building = subscriberRequest.building
//
//        val subscriberSave: SubscriberEntity = subscriberRepository.save(subscriber)
//
//        tempStreet.subscribers?.add(subscriberSave)
//        tempDistrict.subscribers?.add(subscriberSave)
//
//        return subscriberSave.convertToResponseDto()
//    }
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
