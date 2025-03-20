package org.bagirov.reportservice.entity

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "report_publication")
data class PublicationEntity(
    @Id
    @Column(name = "publication_id", nullable = false)
    val publicationId: UUID,

    @Column(name = "index", nullable = false, unique = true)
    var index: String,

    @Column(name = "author", nullable = false)
    var author: String,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "type", nullable = false)
    var type: String,

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    @Column(name = "count_subscriber", nullable = false)
    var countSubscriber: Int = 0
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

        return publicationId != null && publicationId == other.publicationId
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  publicationId = $publicationId   ,   index = $index   ,   author = $author   ,   title = $title   ,   type = $type   ,   price = $price   ,   countSubscriber = $countSubscriber )"
    }
}
