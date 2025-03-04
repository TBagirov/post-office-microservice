package org.bagirov.authservice.service

import org.bagirov.authservice.dto.PostmanUpdatedEventDto
import org.bagirov.authservice.dto.request.UserUpdateRequest
import org.bagirov.authservice.dto.response.UserResponse
import org.bagirov.authservice.props.Role
import org.bagirov.authservice.repository.UserRepository
import org.bagirov.authservice.utill.convertToResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class UserService (
    private val userRepository: UserRepository,
    private val kafkaProducerService: KafkaProducerService
){

    fun getById(id: UUID): UserResponse = userRepository.findById(id)
        .orElseThrow{ NoSuchElementException("User with ID ${id} not found") }
        .convertToResponseDto()

    fun getAll():List<UserResponse> = userRepository.findAll().map{ it.convertToResponseDto()}

    fun update(userUpdate: UserUpdateRequest): UserResponse {
        val user = userRepository.findById(userUpdate.userId)
            .orElseThrow { NoSuchElementException("User with ID ${userUpdate.userId} not found") }

        // Обновляем время обновления
        user.apply {
            userUpdate.name?.let { name = it  }
            userUpdate.surname?.let { surname = it }
            userUpdate.patronymic?. let { patronymic = it }
            userUpdate.email?.let { email = it }
            userUpdate.phone?.let { phone = it }
            updatedAt = LocalDateTime.now()
        }

        val updatedUser = userRepository.save(user)

        // Отправляем событие об обновлении почтальона
        if (user.role.name == Role.POSTMAN) {
            kafkaProducerService.sendPostmanUpdatedEvent(
                PostmanUpdatedEventDto(
                    userId = user.id!!,
                    updatedAt = updatedUser.updatedAt!!.toInstant(ZoneOffset.UTC).toEpochMilli()
                )
            )
        }

        return updatedUser.convertToResponseDto()
    }


    @Transactional
    fun delete(id: UUID): UserResponse {
        // Найти существующего пользователя
        val existingUser = userRepository.findById(id)
            .orElseThrow { NoSuchElementException("User with ID ${id} not found") }

        existingUser.tokens = null

        // Удалить пользователя
        userRepository.delete(existingUser)

        kafkaProducerService.sendUserDeletedEvent(existingUser.id!!)

        return existingUser.convertToResponseDto()
    }

}