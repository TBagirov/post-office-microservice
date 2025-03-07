package org.bagirov.postalservice.repository


import org.bagirov.postalservice.entity.StreetEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StreetRepository: JpaRepository<StreetEntity, UUID> {


    fun findByName(name: String): StreetEntity?


}