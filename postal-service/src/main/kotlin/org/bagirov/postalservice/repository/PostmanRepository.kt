package org.bagirov.postalservice.repository


import org.bagirov.postalservice.entity.PostmanEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostmanRepository: JpaRepository<PostmanEntity, UUID> {
    fun findByUserId(userId: UUID): PostmanEntity?
}