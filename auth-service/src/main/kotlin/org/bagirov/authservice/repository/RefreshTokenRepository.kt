package org.bagirov.authservice.repository

import org.bagirov.authservice.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, UUID> {
    fun findAllByToken(token: String): MutableList<RefreshTokenEntity>

    fun findAllByUserId(userId: UUID): MutableList<RefreshTokenEntity>
}