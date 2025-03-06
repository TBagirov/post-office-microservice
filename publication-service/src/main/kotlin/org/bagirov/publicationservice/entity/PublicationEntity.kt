package org.bagirov.publicationservice.entity

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "publications")
data class PublicationEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name ="index", nullable = false, unique = true)
    var index: String,

    @Column(nullable = false)
    var title: String,

    var description: String? = null,

    @Column(nullable = false)
    var price: BigDecimal,

    @Column(nullable = false)
    var author: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    var type: PublicationTypeEntity
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as PublicationEntity

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   title = $title   ,   description = $description   ,   price = $price   ,   author = $author )"
    }
}

