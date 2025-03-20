package org.bagirov.authservice.service

import io.jsonwebtoken.JwtException
import io.mockk.*
import jakarta.servlet.http.HttpServletResponse
import org.bagirov.authservice.client.PostalServiceClient
import org.bagirov.authservice.dto.UserBecomeSubscriberEventDto
import org.bagirov.authservice.dto.request.AuthenticationRequest
import org.bagirov.authservice.dto.request.RegistrationRequest
import org.bagirov.authservice.entity.RefreshTokenEntity
import org.bagirov.authservice.entity.RoleEntity
import org.bagirov.authservice.entity.UserEntity
import org.bagirov.authservice.props.Role
import org.bagirov.authservice.repository.RefreshTokenRepository
import org.bagirov.authservice.repository.RoleRepository
import org.bagirov.authservice.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class AuthenticationServiceTest {

    private lateinit var authenticationService: AuthenticationService
    private val userRepository: UserRepository = mockk()
    private val roleRepository: RoleRepository = mockk()
    private val jwtService: JwtService = mockk()
    private val authenticationManager: AuthenticationManager = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val refreshTokenRepository: RefreshTokenRepository = mockk()
    private val kafkaProducerService: KafkaProducerService = mockk(relaxed = true)
    private val postalServiceClient: PostalServiceClient = mockk()
    private val response: HttpServletResponse = mockk(relaxed = true)

    private val testUser = UserEntity(
        id = UUID.randomUUID(),
        username = "testuser",
        password = "password",
        name = "Test",
        surname = "User",
        patronymic = "Middle",
        email = "test@example.com",
        phone = "+71234567890",
        createdAt = LocalDateTime.now(),
        role = RoleEntity(id = UUID.randomUUID(), name = Role.GUEST)
    )

    @BeforeEach
    fun setUp() {
        authenticationService = AuthenticationService(
            userRepository,
            roleRepository,
            jwtService,
            authenticationManager,
            passwordEncoder,
            refreshTokenRepository,
            kafkaProducerService,
            postalServiceClient
        )
    }

    @Test
    fun `should authenticate user and return token`() {
        every { userRepository.findByUsername("testuser") } returns Optional.of(testUser)
        every { authenticationManager.authenticate(any()) } returns UsernamePasswordAuthenticationToken(testUser.username, testUser.password)
        every { jwtService.createAccessToken(testUser) } returns "access-token"
        every { jwtService.createRefreshToken(testUser) } returns "refresh-token"
        every { refreshTokenRepository.save(any()) } returns RefreshTokenEntity(token = "refresh-token", user = testUser)

        val request = AuthenticationRequest("testuser", "password")
        val response = authenticationService.authorization(request, response)

        assertEquals("access-token", response.accessToken)
        assertEquals("testuser", response.username)
    }

    @Test
    fun `should throw exception when user not found during authentication`() {
        every { userRepository.findByUsername("unknown") } returns Optional.empty()

        val request = AuthenticationRequest("unknown", "password")
        val exception = assertThrows<NoSuchElementException> {
            authenticationService.authorization(request, response)
        }

        assertEquals("User is not registered", exception.message)
    }

    @Test
    fun `should register new user and return tokens`() {
        val role = RoleEntity(UUID.randomUUID(), Role.GUEST, mutableSetOf())
        every { roleRepository.findByName(Role.GUEST) } returns role
        every { passwordEncoder.encode("password") } returns "encoded-password"

        every { userRepository.save(any()) } answers {
            firstArg<UserEntity>().copy(id = UUID.randomUUID()) // Генерация ID
        }

        every { jwtService.createAccessToken(any()) } returns "access-token"
        every { jwtService.createRefreshToken(any()) } returns "refresh-token"
        every { refreshTokenRepository.save(any()) } returns RefreshTokenEntity(token = "refresh-token", user = testUser)
        every { userRepository.existsByUsername("testuser") } returns false
        every { userRepository.existsByEmail("test@example.com") } returns false
        every { userRepository.existsByPhone("+71234567890") } returns false

        val request = RegistrationRequest("Test", "User", "Middle", "testuser", "password", "test@example.com", "+71234567890")
        val response = authenticationService.registration(request, response)

        assertEquals("access-token", response.accessToken)
        assertEquals("testuser", response.username)
    }

    @Test
    fun `should throw exception when trying to refresh invalid token`() {
        every { jwtService.getUsername("invalid-token") } throws JwtException("Token is empty")

        val exception = assertThrows<JwtException> {
            authenticationService.refresh("invalid-token", response)
        }

        assertEquals("Token is empty", exception.message)
    }

    @Test
    fun `should log out user and remove refresh token`() {
        every { refreshTokenRepository.findAllByToken("refresh-token") } returns mutableListOf(
            RefreshTokenEntity(token = "refresh-token", user = testUser)
        )
        every { refreshTokenRepository.delete(any()) } just Runs

        val response = authenticationService.logout("refresh-token", response)
        assertEquals("Logout successful", response["message"])
    }

    @Test
    fun `should send event when user becomes subscriber`() {
        val roleSubscriber = RoleEntity(id = UUID.randomUUID(), name = Role.SUBSCRIBER)
        every { roleRepository.findByName(Role.SUBSCRIBER) } returns roleSubscriber
        every { userRepository.findById(testUser.id!!) } returns Optional.of(testUser)
        every { userRepository.save(any()) } returns testUser.copy(role = roleSubscriber)
        every { postalServiceClient.getStreetAndDistrict("TestStreet") } returns mockk {
            every { streetId } returns UUID.randomUUID()
            every { districtId } returns UUID.randomUUID()
        }
        every { kafkaProducerService.sendUserBecameSubscriberEvent(any()) } just Runs

        authenticationService.becomeSubscriber(testUser, mockk {
            every { streetName } returns "TestStreet"
            every { building } returns "123"
            every { subAddress } returns "Apt1"
        })

        verify { kafkaProducerService.sendUserBecameSubscriberEvent(any<UserBecomeSubscriberEventDto>()) }
    }
}
