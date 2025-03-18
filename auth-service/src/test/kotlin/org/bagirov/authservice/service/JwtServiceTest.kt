package org.bagirov.authservice.service

import org.bagirov.authservice.entity.RoleEntity
import org.bagirov.authservice.entity.UserEntity
import org.bagirov.authservice.props.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class JwtServiceTest {

    @Mock
    private lateinit var jwtService: JwtService

    private lateinit var testUser: UserEntity
    private val accessToken = "mocked-access-token"
    private val refreshToken = "mocked-refresh-token"

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
            createdAt = java.time.LocalDateTime.now(),
            role = RoleEntity(id = UUID.randomUUID(), name = Role.GUEST) // исправлено
        )
    }

    @Test
    fun `should generate access token`() {
        `when`(jwtService.createAccessToken(testUser)).thenReturn(accessToken)

        val result = jwtService.createAccessToken(testUser)

        assertNotNull(result)
        assertEquals(accessToken, result)
    }

    @Test
    fun `should generate refresh token`() {
        `when`(jwtService.createRefreshToken(testUser)).thenReturn(refreshToken)

        val result = jwtService.createRefreshToken(testUser)

        assertNotNull(result)
        assertEquals(refreshToken, result)
    }

    @Test
    fun `should extract username from token`() {
        `when`(jwtService.getUsername(accessToken)).thenReturn(testUser.username)

        val result = jwtService.getUsername(accessToken)

        assertNotNull(result)
        assertEquals(testUser.username, result)
    }
}
