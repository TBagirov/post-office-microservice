package org.bagirov.authservice.service

import org.bagirov.authservice.dto.request.UserUpdateRequest
import org.bagirov.authservice.entity.RoleEntity
import org.bagirov.authservice.entity.UserEntity
import org.bagirov.authservice.props.Role
import org.bagirov.authservice.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var kafkaProducerService: KafkaProducerService  // Добавили мок

    @InjectMocks
    private lateinit var userService: UserService

    private lateinit var testUser: UserEntity

    @BeforeEach
    fun setup() {
        testUser = UserEntity(
            id = UUID.randomUUID(),
            name = "John",
            surname = "Doe",
            patronymic = "Test",
            username = "johndoe",
            password = "password",
            email = "john@example.com",
            phone = "+1234567890",
            createdAt = LocalDateTime.now(),
            role = RoleEntity(id = UUID.randomUUID(), name = Role.GUEST)
        )
    }

    @Test
    fun `getById should return user response when user exists`() {
        `when`(userRepository.findById(testUser.id!!)).thenReturn(Optional.of(testUser))

        val result = userService.getById(testUser.id!!)

        assertNotNull(result)
        assertEquals(testUser.id, result.id)
        assertEquals(testUser.username, result.username)
    }

    @Test
    fun `getById should throw NoSuchElementException when user does not exist`() {
        `when`(userRepository.findById(any(UUID::class.java))).thenReturn(Optional.empty())

        assertThrows(NoSuchElementException::class.java) {
            userService.getById(UUID.randomUUID())
        }
    }

    @Test
    fun `update should update user details and return updated response`() {
        val updateRequest = UserUpdateRequest(name = "Updated", surname = null, patronymic = null, email = null, phone = null)
        val updatedUser = testUser.copy(name = "Updated", updatedAt = LocalDateTime.now())

        `when`(userRepository.findById(testUser.id!!)).thenReturn(Optional.of(testUser))
        `when`(userRepository.save(any(UserEntity::class.java))).thenReturn(updatedUser)

        val result = userService.update(testUser, updateRequest)

        assertEquals("Updated", result.name)
        assertNotNull(result.updatedAt)
    }
}
