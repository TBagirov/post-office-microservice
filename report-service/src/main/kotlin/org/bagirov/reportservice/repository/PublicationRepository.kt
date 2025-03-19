package org.bagirov.reportservice.repository

import org.bagirov.reportservice.entity.PublicationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PublicationRepository : JpaRepository<PublicationEntity, UUID> {
    fun findByIndex(index: String): PublicationEntity?
    fun findByPublicationId(publicationId: UUID): PublicationEntity?
}