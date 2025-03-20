package org.bagirov.notificationservice.service

import mu.KotlinLogging
import org.bagirov.notificationservice.dto.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class NotificationService(
    private val emailService: EmailService,
) {
    private val log = KotlinLogging.logger {}

    fun sendNotificationEmail(event: NotificationEvent) {
        log.info { "start send notification email" }

        val formatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy")

        when (event) {
            is SubscriptionConfirmedEvent -> emailService.sendEmail(
                event.email,
                "Подтверждение подписки",
                "subscription-confirmed.html",
                mapOf(
                    "username" to event.username,
                    "publicationName" to event.publicationName,
                    "startDate" to LocalDateTime.parse(event.startDate).format(formatter),
                    "duration" to event.duration
                )
            )
            is PostmanAssignedEvent -> emailService.sendEmail(
                event.email,
                "Назначен новый район",
                "postman-assigned.html",
                mapOf(
                    "username" to event.username,
                    "districtName" to event.districtName
                )
            )
            is SubscriptionExpiredEvent -> emailService.sendEmail(
                event.email,
                "Подписка истекла",
                "subscription-expired.html",
                mapOf(
                    "publicationName" to event.publicationName,
                    "expirationDate" to LocalDateTime.parse(event.expirationDate).format(formatter)
                )
            )
            is SubscriptionCancelledEvent -> emailService.sendEmail(
                event.email,
                "Подписка отменена",
                "subscription-cancelled.html",
                mapOf(
                    "publicationName" to event.publicationName,
                    "cancellationReason" to event.cancellationReason
                )
            )
        }
    }
}
