package org.bagirov.subscriberservice.entity

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name="subscribers")
data class SubscriberEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "user_id", nullable = false, unique = true)
    var userId: UUID,

    @Column(name = "district_id")
    var districtId: UUID?,

    @Column(name = "street_id")
    var streetId: UUID?,

    @Column(name = "building", nullable = false)
    var building: String,

    @Column(name = "sub_address")
    var subAddress: String?,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

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

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   userId = $userId   ,   districtId = $districtId   ,   streetId = $streetId   ,   building = $building   ,   subAddress = $subAddress   ,   createdAt = $createdAt   ,   updatedAt = $updatedAt )"
    }

}