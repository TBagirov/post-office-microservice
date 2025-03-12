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
            // Получаем информацию о публикации
            val publication: PublicationResponseClient = publicationServiceClient.getPublication(event.publicationId)

            // Вычисляем стоимость подписки
            val totalAmount = publication.price.multiply(BigDecimal(event.duration)) // BigDecimal умножаем правильно

            // Создаём запись о платеже со статусом PENDING
            val payment = PaymentEntity(
                subscriberId = event.subscriberId,
                subscriptionId = event.subscriptionId,
                amount = totalAmount,
                status = PaymentStatus.PENDING
            )

            log.info { "Начинаем обработку платежа для подписки ${event.subscriptionId}" }
            paymentRepository.save(payment)
            log.info { "Платеж ${payment.id} завершен со статусом ${payment.status}" }

            // Имитация процесса оплаты (можно заменить на реальную логику)
            val isPaymentSuccessful = processExternalPayment(totalAmount)

            // Обновляем статус оплаты
            payment.status = if (isPaymentSuccessful) PaymentStatus.SUCCESS else PaymentStatus.FAILED
            paymentRepository.save(payment)

            // Отправляем событие в SubscriptionService
            val paymentEvent = SubscriptionPaymentEvent(
                subscriptionId = event.subscriptionId,
                status = if (isPaymentSuccessful) SubscriptionStatus.ACTIVE else SubscriptionStatus.CANCELLED
            )

            paymentEventProducer.sendPaymentEvent(paymentEvent)

            log.info { "Оплата для подписки ${event.subscriptionId} ${if (isPaymentSuccessful) "успешно проведена" else "не удалась"}." }

        } catch (e: Exception) {
            log.error(e) { "Ошибка обработки платежа для подписки ${event.subscriptionId}: ${e.message}" }

            // Если произошла ошибка, отправляем событие об отмене подписки
            val failedEvent = SubscriptionPaymentEvent(
                subscriptionId = event.subscriptionId,
                status = SubscriptionStatus.CANCELLED
            )

            paymentEventProducer.sendPaymentEvent(failedEvent)
        }
    }
    /**
     * Имитация внешней системы платежей.
     * Можно заменить на реальный вызов API платежного провайдера.
     */
    private fun processExternalPayment(amount: BigDecimal): Boolean {
        return amount < BigDecimal(5000) // Например, платежи выше 5000 рублей отклоняются
    }
}