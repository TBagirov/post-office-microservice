package org.bagirov.authservice.service

import mu.KotlinLogging
import org.bagirov.authservice.dto.PostmanUpdatedEventDto
import org.bagirov.authservice.dto.request.UserUpdateRequest
import org.bagirov.authservice.dto.response.UserResponse
import org.bagirov.authservice.entity.UserEntity
import org.bagirov.authservice.props.Role
import org.bagirov.authservice.repository.UserRepository
import org.bagirov.authservice.utill.convertToEventDto
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

    private val log = KotlinLogging.logger {}

    fun getById(id: UUID): UserResponse {
        log.info { "Fetching user by ID: $id" }
        return userRepository.findById(id)
            .orElseThrow {
                log.error { "User with ID $id not found" }
                NoSuchElementException("User with ID $id not found")
            }
            .convertToResponseDto()
    }

    fun getAll(): List<UserResponse> {
        log.info { "Fetching all users" }
        return userRepository.findAll().map { it.convertToResponseDto() }
    }

    fun update(currentUser: UserEntity, userUpdate: UserUpdateRequest): UserResponse {
        log.info { "Updating user: ${currentUser.id}" }
        val user = userRepository.findById(currentUser.id!!)
            .orElseThrow {
                log.error { "User with ID ${currentUser.id} not found" }
                NoSuchElementException("User with ID ${currentUser.id} not found")
            }

        user.apply {
            userUpdate.name?.let { name = it }
            userUpdate.surname?.let { surname = it }
            userUpdate.patronymic?.let { patronymic = it }
            userUpdate.email?.let { email = it }
            userUpdate.phone?.let { phone = it }
            updatedAt = LocalDateTime.now()
        }

        val updatedUser = userRepository.save(user)
        log.info { "User updated successfully: ${user.id}" }

        kafkaProducerService.sendUserUpdatedEvent(updatedUser.convertToEventDto())

        if (user.role.name == Role.POSTMAN) {
            log.info { "Sending postman updated event for user: ${user.id}" }
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
        log.info { "Deleting user: $id" }
        val existingUser = userRepository.findById(id)
            .orElseThrow {
                log.error { "User with ID $id not found" }
                NoSuchElementException("User with ID $id not found")
            }

        existingUser.tokens = null
        userRepository.delete(existingUser)
        log.info { "User deleted successfully: $id" }

        kafkaProducerService.sendUserDeletedEvent(existingUser.id!!)
        return existingUser.convertToResponseDto()
    }
}