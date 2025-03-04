package org.bagirov.postalservice.repository


import org.bagirov.postalservice.entity.RegionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RegionRepository: JpaRepository<RegionEntity, UUID> {

}