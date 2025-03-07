package org.bagirov.publicationservice.repository


import org.bagirov.publicationservice.entity.PublicationTypeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PublicationTypeRepository: JpaRepository<PublicationTypeEntity, UUID> {


    fun findByName(type: String): PublicationTypeEntity?


}