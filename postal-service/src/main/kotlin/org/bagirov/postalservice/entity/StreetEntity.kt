package org.bagirov.postalservice.entity

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.util.*

@Entity
@Table(name="streets")
data class StreetEntity(

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "name", nullable=false, unique = true)
    var name: String,

    @ManyToOne
    @JoinColumn(name="region_id", referencedColumnName = "id")
    var region: RegionEntity? = null,

    ) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as StreetEntity

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   name = $name   ,   region = $region )"
    }
}
