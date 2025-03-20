package org.bagirov.subscriptionservice.props

enum class SubscriptionStatus {
    PENDING_PAYMENT, // Ожидание оплаты
    ACTIVE,          // Активная подписка
    CANCELLED,       // Отменена
    EXPIRED          // Истекла
}
