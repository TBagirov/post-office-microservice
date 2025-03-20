package org.bagirov.postalservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bagirov.postalservice.dto.UserEventDto
import org.bagirov.postalservice.repository.PostmanRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class KafkaConsumerServiceTest {

    private val postmanRepository: PostmanRepository = mockk()
    private val objectMapper: ObjectMapper = mockk()
    private lateinit var kafkaConsumerService: KafkaConsumerService

    @BeforeEach
    fun setUp() {
        kafkaConsumerService = KafkaConsumerService(postmanRepository, objectMapper)
    }

    @Test
    fun `should consume user created event and create postman`() {
        val eventJson = "{\"id\":\"${UUID.randomUUID()}\",\"role\":\"POSTMAN\"}"
        val event = UserEventDto(UUID.randomUUID(), LocalDateTime.now(), "POSTMAN")
        every { objectMapper.readValue(eventJson, UserEventDto::class.java) } returns event
        every { postmanRepository.save(any()) } returns mockk()

        kafkaConsumerService.consumeUserCreatedEvent(eventJson)

        verify { postmanRepository.save(any()) }
    }
}
