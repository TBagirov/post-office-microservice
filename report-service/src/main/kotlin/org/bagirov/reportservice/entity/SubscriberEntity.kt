package org.bagirov.reportservice.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy
import java.util.*

@Entity
@Table(name = "report_subscriber")
data class SubscriberEntity(
    @Id
    @Column(name = "subscriber_id", nullable = false)
    val subscriberId: UUID,

    @Column(name="user_id", nullable = false, unique = true)
    val userId: UUID,

    @Column(name = "username", nullable = false)
    val username: String,

    @Column(name="name", nullable = false)
    var name: String,

    @Column(name="surname", nullable = false)
    var surname: String,

    @Column(name="patronymic", nullable = false)
    var patronymic: String,
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as SubscriberEntity

        return subscriberId != null && subscriberId == other.subscriberId
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  subscriberId = $subscriberId   ,   username = $username   ,   name = $name   ,   surname = $surname   ,   patronymic = $patronymic )"
    }
}


