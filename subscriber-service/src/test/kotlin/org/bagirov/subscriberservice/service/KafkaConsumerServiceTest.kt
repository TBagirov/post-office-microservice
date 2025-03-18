package org.bagirov.subscriberservice.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import org.bagirov.subscriberservice.dto.UserBecomeSubscriberEventDto
import org.bagirov.subscriberservice.entity.SubscriberEntity
import org.bagirov.subscriberservice.repository.SubscriberRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneOffset
import java.util.*
import kotlin.test.assertNotNull

class KafkaConsumerServiceTest {

    private lateinit var kafkaConsumerService: KafkaConsumerService
    private val subscriberRepository: SubscriberRepository = mockk(relaxed = true)
    private val objectMapper: ObjectMapper = mockk()

    @BeforeEach
    fun setUp() {
        kafkaConsumerService = KafkaConsumerService(subscriberRepository, objectMapper)
    }

    @Test
    fun `should consume user became subscriber event and save subscriber`() {
        val message = """{
            "userId": "550e8400-e29b-41d4-a716-446655440000",
            "streetId": "550e8400-e29b-41d4-a716-446655440001",
            "districtId": "550e8400-e29b-41d4-a716-446655440002",
            "building": "25A",
            "subAddress": "1",
            "createdAt": 1710710400000
        }""".trimIndent()

        val userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val streetId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
        val districtId = UUID.fromString("550e8400-e29b-41d4-a716-446655440002")

        val expectedEvent = UserBecomeSubscriberEventDto(userId, streetId, districtId, "25A", "1", 1710710400000)
        every { objectMapper.readValue(message, UserBecomeSubscriberEventDto::class.java) } returns expectedEvent
        every { subscriberRepository.save(any()) } returnsArgument 0

        kafkaConsumerService.consumeUserBecameSubscriberEvent(message)

        verify {
            subscriberRepository.save(
                withArg { subscriber ->
                    assertNotNull(subscriber)
                    assert(subscriber.userId == expectedEvent.userId)
                    assert(subscriber.streetId == expectedEvent.streetId)
                    assert(subscriber.districtId == expectedEvent.districtId)
                    assert(subscriber.building == expectedEvent.building)
                    assert(subscriber.subAddress == expectedEvent.subAddress)
                    assert(subscriber.createdAt == Instant.ofEpochMilli(expectedEvent.createdAt).atZone(ZoneOffset.UTC).toLocalDateTime())
                }
            )
        }
    }

    @Test
    fun `should consume user deleted event and delete subscriber`() {
        val message = """{"id": "550e8400-e29b-41d4-a716-446655440000"}"""
        val userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")

        val subscriber = SubscriberEntity(
            id = UUID.randomUUID(),
            userId = userId,
            streetId = null,
            districtId = null,
            building = "25A",
            subAddress = null,
            createdAt = Instant.now().atZone(ZoneOffset.UTC).toLocalDateTime()
        )

        // Исправленное мокирование `ObjectMapper`
        every { objectMapper.readValue<Map<String, String>>(message, any<TypeReference<Map<String, String>>>()) } returns mapOf("id" to userId.toString())

        every { subscriberRepository.findByUserId(userId) } returns subscriber
        justRun { subscriberRepository.delete(subscriber) }

        kafkaConsumerService.consumeUserDeletedEvent(message)

        verify { subscriberRepository.delete(subscriber) }
    }

    @Test
    fun `should handle case when subscriber not found on user deleted event`() {
        val message = """{"id": "550e8400-e29b-41d4-a716-446655440000"}"""
        val userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")

        every { objectMapper.readValue(message, object : TypeReference<Map<String, String>>() {}) } returns mapOf("id" to userId.toString())
        every { subscriberRepository.findByUserId(userId) } returns null

        kafkaConsumerService.consumeUserDeletedEvent(message)

        verify(exactly = 0) { subscriberRepository.delete(any()) }
    }
}