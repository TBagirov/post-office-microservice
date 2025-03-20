package org.bagirov.paymentservice.entity

import jakarta.persistence.*
import org.bagirov.paymentservice.props.PaymentStatus
import org.hibernate.proxy.HibernateProxy
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "payments")
data class PaymentEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "subscriber_id", nullable = false)
    val subscriberId: UUID,

    @Column(name = "subscription_id", nullable = false)
    val subscriptionId: UUID,

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING) // Сохраняем как строку в БД
    @Column(name = "status", nullable = false)
    var status: PaymentStatus = PaymentStatus.PENDING, // По умолчанию "PENDING"

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as PaymentEntity

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   subscriberId = $subscriberId   ,   subscriptionId = $subscriptionId   ,   amount = $amount   ,   status = $status   ,   createdAt = $createdAt   ,   updatedAt = $updatedAt )"
    }
}
