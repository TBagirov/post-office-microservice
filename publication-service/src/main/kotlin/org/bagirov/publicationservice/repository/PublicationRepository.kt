package org.bagirov.publicationservice.repository


import org.bagirov.publicationservice.entity.PublicationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PublicationRepository: JpaRepository<PublicationEntity, UUID> {

}