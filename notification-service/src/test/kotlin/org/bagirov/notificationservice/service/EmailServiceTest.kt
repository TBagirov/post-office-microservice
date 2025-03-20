package org.bagirov.notificationservice.service

import io.mockk.*
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

class EmailServiceTest {

    private lateinit var emailService: EmailService
    private val mailSender: JavaMailSender = mockk(relaxed = true)
    private val templateEngine: TemplateEngine = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        emailService = EmailService(mailSender, templateEngine)
    }

    @Test
    fun `should send email successfully`() {
        // Arrange
        val mimeMessage: MimeMessage = mockk(relaxed = true)

        every { mailSender.createMimeMessage() } returns mimeMessage
        every { templateEngine.process(any<String>(), any<Context>()) } returns "<html>Email content</html>"
        every { mailSender.send(mimeMessage) } just Runs

        // Act
        emailService.sendEmail(
            to = "test@example.com",
            subject = "Test Email",
            templateName = "test-template.html",
            variables = mapOf("username" to "John Doe")
        )

        // Assert
        verify { mailSender.createMimeMessage() }
        verify { templateEngine.process("test-template.html", any<Context>()) }
        verify { mailSender.send(mimeMessage) }
    }

    @Test
    fun `should log error when email sending fails`() {
        // Arrange
        val mimeMessage: MimeMessage = mockk(relaxed = true)
        every { mailSender.createMimeMessage() } returns mimeMessage
        every { templateEngine.process(any<String>(), any<Context>()) } returns "<html>Email content</html>"
        every { mailSender.send(mimeMessage) } throws RuntimeException("SMTP Error")

        // Act
        emailService.sendEmail(
            to = "fail@example.com",
            subject = "Test Email",
            templateName = "test-template.html",
            variables = mapOf("username" to "John Doe")
        )

        // Assert
        verify { mailSender.createMimeMessage() }
        verify { templateEngine.process("test-template.html", any<Context>()) }
        verify { mailSender.send(mimeMessage) }
    }

    @Test
    fun `should send email without logo when logo file is missing`() {
        // Arrange
        val mimeMessage: MimeMessage = mockk(relaxed = true)
        every { mailSender.createMimeMessage() } returns mimeMessage
        every { templateEngine.process(any<String>(), any<Context>()) } returns "<html>Email content</html>"
        every { mailSender.send(mimeMessage) } just Runs

        mockkConstructor(ClassPathResource::class)
        every { anyConstructed<ClassPathResource>().exists() } returns false

        // Act
        emailService.sendEmail(
            to = "test@example.com",
            subject = "Test Email",
            templateName = "test-template.html",
            variables = mapOf("username" to "John Doe")
        )

        // Assert
        verify { mailSender.createMimeMessage() }
        verify { templateEngine.process("test-template.html", any<Context>()) }
        verify { mailSender.send(mimeMessage) }
    }
}
