package org.bagirov.postalservice.repository


import org.bagirov.postalservice.entity.DistrictEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DistrictRepository: JpaRepository<DistrictEntity, UUID> {

    fun findByRegionName(regionName: String): Optional<List<DistrictEntity>>

    fun findByRegionId(regionId: UUID): List<DistrictEntity>?
}