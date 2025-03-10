package org.bagirov.notificationservice.service

import mu.KotlinLogging
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
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            val context = Context()
            context.setVariables(variables)
            val htmlContent = templateEngine.process(templateName, context)

            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(htmlContent, true) // true - HTML-сообщение

            mailSender.send(message)
            log.info { "HTML email sent to $to with subject: $subject" }
        } catch (e: Exception) {
            log.error(e) { "Error sending email to $to: ${e.message}" }
        }
    }
}
