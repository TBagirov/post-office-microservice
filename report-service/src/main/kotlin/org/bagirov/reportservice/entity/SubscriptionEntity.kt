package org.bagirov.reportservice.entity

import jakarta.persistence.*
import org.bagirov.reportservice.props.SubscriptionStatus
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "report_subscription")
data class SubscriptionEntity(
    @Id
    @Column(name = "subscription_id", nullable = false)
    val subscriptionId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id", nullable = false)
    val subscriber: SubscriberEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_id", nullable = false)
    val publication: PublicationEntity,

    @Column(name = "start_date_subscription", nullable = false)
    val startDateSubscription: LocalDateTime,

    @Column(name = "end_date_subscription", nullable = false)
    val endDateSubscription: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: SubscriptionStatus
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as SubscriptionEntity

        return subscriptionId != null && subscriptionId == other.subscriptionId
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  subscriptionId = $subscriptionId   ,   startDateSubscription = $startDateSubscription   ,   endDateSubscription = $endDateSubscription   ,   status = $status )"
    }
}
