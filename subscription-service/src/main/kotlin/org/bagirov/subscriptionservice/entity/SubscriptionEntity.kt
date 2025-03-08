package org.bagirov.subscriptionservice.entity

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name="subscriptions")
data class SubscriptionEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name="subscriber_id", nullable = false)
    val subscriberId: UUID,

    @Column(name="publication_id", nullable = false)
    val publicationId: UUID,

    @Column(name="start_date", nullable = false)
    var startDate: LocalDateTime = LocalDateTime.now(),

    @Column(name="duration", nullable = false)
    val duration: Int, // Используем Period для хранения срока

    @Column(name="created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name="updated_at")
    var updatedAt: LocalDateTime? = null

){
    fun getEndDate(): LocalDateTime {
        return startDate.plusMonths(duration.toLong())
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as SubscriptionEntity

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   subscriberId = $subscriberId   ,   publicationId = $publicationId   ,   startDate = $startDate   ,   duration = $duration   ,   createdAt = $createdAt   ,   updatedAt = $updatedAt )"
    }

}
