package org.bagirov.paymentservice.service

import mu.KotlinLogging
import org.bagirov.paymentservice.client.PublicationServiceClient
import org.bagirov.paymentservice.dto.SubscriptionCreatedEvent
import org.bagirov.paymentservice.dto.SubscriptionPaymentEvent
import org.bagirov.paymentservice.dto.response.client.PublicationResponseClient
import org.bagirov.paymentservice.entity.PaymentEntity
import org.bagirov.paymentservice.props.PaymentStatus
import org.bagirov.paymentservice.props.SubscriptionStatus
import org.bagirov.paymentservice.repository.PaymentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val paymentEventProducer: KafkaProducerService,
    private val publicationServiceClient: PublicationServiceClient,
) {

    private val log = KotlinLogging.logger {}

    @Transactional
    fun processPayment(event: SubscriptionCreatedEvent) {
        try {
            log.info { "Processing payment for subscription ${event.subscriptionId}" }

            // Получаем информацию о публикации
            val publication: PublicationResponseClient = publicationServiceClient.getPublication(event.publicationId)
            log.info { "Fetched publication details for publication ID: ${event.publicationId}" }

            // Вычисляем стоимость подписки
            val totalAmount = publication.price.multiply(BigDecimal(event.duration)) // BigDecimal умножаем правильно
            log.info { "Calculated total payment amount: $totalAmount" }

            // Создаём запись о платеже со статусом PENDING
            val payment = PaymentEntity(
                subscriberId = event.subscriberId,
                subscriptionId = event.subscriptionId,
                amount = totalAmount,
                status = PaymentStatus.PENDING
            )

            log.info { "Start payment, subscription: ${event.subscriptionId}" }
            paymentRepository.save(payment)
            log.info { "Payment ${payment.id} created with status ${payment.status}" }

            // Имитация процесса оплаты (можно заменить на реальную логику)
            val isPaymentSuccessful = processExternalPayment(totalAmount)
            log.info { "Payment ${payment.id} created with status ${payment.status}" }

            // Обновляем статус оплаты
            payment.status = if (isPaymentSuccessful) PaymentStatus.SUCCESS else PaymentStatus.FAILED
            paymentRepository.save(payment)
            log.info { "Updated payment ${payment.id} status to ${payment.status}" }

            // Отправляем событие в SubscriptionService
            val paymentEvent = SubscriptionPaymentEvent(
                subscriptionId = event.subscriptionId,
                status = if (isPaymentSuccessful) SubscriptionStatus.ACTIVE else SubscriptionStatus.CANCELLED
            )

            paymentEventProducer.sendPaymentEvent(paymentEvent)

            log.info { "Payment event sent for subscription ${event.subscriptionId} with status ${paymentEvent.status}" }

        } catch (e: Exception) {
            log.error(e) { "Error processing payment for subscription ${event.subscriptionId}: ${e.message}" }

            // Если произошла ошибка, отправляем событие об отмене подписки
            val failedEvent = SubscriptionPaymentEvent(
                subscriptionId = event.subscriptionId,
                status = SubscriptionStatus.CANCELLED
            )

            paymentEventProducer.sendPaymentEvent(failedEvent)
            log.warn { "Subscription ${event.subscriptionId} has been cancelled due to payment processing failure" }
        }
    }

    /**
     * Имитация внешней системы платежей.
     */
    private fun processExternalPayment(amount: BigDecimal): Boolean {
        log.info { "Simulating external payment processing for amount: $amount" }
        return amount < BigDecimal(5000) // Платежи выше 5000 рублей отклоняются
    }
}