package org.bagirov.authservice.service

import org.bagirov.authservice.dto.response.UserResponse
import org.bagirov.authservice.repository.UserRepository
import org.bagirov.authservice.utill.convertToResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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