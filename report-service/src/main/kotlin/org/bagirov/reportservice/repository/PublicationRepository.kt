package org.bagirov.reportservice.repository

import org.bagirov.reportservice.dto.response.ReportPublicationResponse
import org.bagirov.reportservice.entity.PublicationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PublicationRepository : JpaRepository<PublicationEntity, UUID> {
    fun findByIndex(index: String): PublicationEntity?
    fun findByPublicationId(publicationId: UUID): PublicationEntity?

    @Query(nativeQuery = true, value = """
        SELECT 
            publication_id, 
            index, 
            title, 
            author, 
            type, 
            price, 
            count_subscriber 
        FROM report_publication
    """)
    fun getReportPublications(): List<Any>
}