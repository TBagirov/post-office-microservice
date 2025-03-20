package org.bagirov.postalservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bagirov.postalservice.dto.PostmanAssignedEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.kafka.core.KafkaTemplate

class KafkaProducerServiceTest {

    private val kafkaTemplate: KafkaTemplate<String, String> = mockk()
    private val objectMapper: ObjectMapper = mockk()
    private lateinit var kafkaProducerService: KafkaProducerService

    @BeforeEach
    fun setUp() {
        kafkaProducerService = KafkaProducerService(kafkaTemplate, objectMapper)
    }

    @Test
    fun `should send notification event`() {
        val event = PostmanAssignedEvent("test@mail.com", "testuser", "District 1")
        val eventJson = "{\"email\":\"test@mail.com\",\"username\":\"testuser\",\"districtName\":\"District 1\"}"
        every { objectMapper.writeValueAsString(event) } returns eventJson
        every { kafkaTemplate.send("notification-events", eventJson) } returns mockk()

        kafkaProducerService.sendNotificationEvent(event)

        verify { kafkaTemplate.send("notification-events", eventJson) }
    }
}
