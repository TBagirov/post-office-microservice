package org.bagirov.postalservice.entity

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.util.*


@Entity
@Table(name = "regions")
data class RegionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name="name", nullable = false, unique = true)
    var name: String,

    @OneToMany(mappedBy = "region")
    var streets: MutableSet<StreetEntity>? = null,

    @OneToMany(mappedBy = "region", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var districts: MutableList<DistrictEntity>? = null

){
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as RegionEntity

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   name = $name )"
    }

// override fun toString(): String {return name}
}
