package org.bagirov.authservice.entity

import jakarta.persistence.*
import org.bagirov.authservice.entity.RefreshTokenEntity
import org.bagirov.authservice.entity.RoleEntity
import org.hibernate.proxy.HibernateProxy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(name = "users")
data class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name="name", nullable = false)
    var name: String,

    @Column(name="surname", nullable = false)
    var surname: String,

    @Column(name="patronymic", nullable = false)
    var patronymic: String,

    @Column(name = "username", nullable = false)
    private var username: String,

    @Column(name = "password", nullable = false)
    private var password: String,

    @Column(name = "email", nullable = true)
    var email: String,

    @Column(name = "phone", nullable = false)
    var phone: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    var role: RoleEntity,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    var tokens: MutableList<RefreshTokenEntity>? = mutableListOf(),

//    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE])
//    var postman: PostmanEntity? = null,
//
//    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE])
//    var subscriber: SubscriberEntity? = null

) : UserDetails{

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority(role.name))

    override fun getPassword(): String {
        return this.password
    }

    override fun getUsername(): String {
        return this.username
    }

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    fun getFio() = listOfNotNull(surname, name, patronymic)
        .joinToString(" ")

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as UserEntity

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   name = $name   ,   surname = $surname   ,   patronymic = $patronymic   ,   username = $username   ,   password = $password   ,   email = $email   ,   phone = $phone   ,   createdAt = $createdAt   ,   role = $role )"
    }

}
