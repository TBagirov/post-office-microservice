package org.bagirov.notificationservice.service

import jakarta.mail.internet.MimeMessage
import mu.KotlinLogging
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine
) {
    private val log = KotlinLogging.logger {}

    fun sendEmail(to: String, subject: String, templateName: String, variables: Map<String, Any>) {
        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            val context = Context()
            context.setVariables(variables)

            // 🏆 Попытка загрузить HTML шаблон
            val htmlContent = templateEngine.process(templateName, context)
            if (htmlContent.isBlank()) {
                log.error("HTML template is empty or not found: $templateName")
                throw IllegalStateException("Пустой HTML шаблон: $templateName")
            }

            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(htmlContent, true) // true - это HTML

            // 🏆 Проверка наличия логотипа и добавление вложения
            val logoPath = "static/logo1.png"
            val logoResource = ClassPathResource(logoPath)

            if (logoResource.exists()) {
                helper.addInline("logoImage", logoResource)
            } else {
                log.warn { "Logo file not found at $logoPath, email will be sent without it." }
            }

            mailSender.send(message)
            log.info { "HTML email sent to $to with subject: $subject" }
        } catch (e: Exception) {
            log.error(e) { "Ошибка отправки email на $to: ${e.message}" }
        }
    }
}
