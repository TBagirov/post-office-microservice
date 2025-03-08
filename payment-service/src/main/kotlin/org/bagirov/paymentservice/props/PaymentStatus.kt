package org.bagirov.paymentservice.props

enum class PaymentStatus(val status: String) {
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    REFUNDED("REFUNDED");
}