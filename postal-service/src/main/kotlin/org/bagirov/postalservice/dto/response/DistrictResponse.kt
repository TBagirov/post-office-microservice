package org.bagirov.postalservice.dto.response

import java.util.*

data class DistrictResponse (
    val id: UUID,
//    val postmanName: String?,
    val postmanId: UUID?,
    val regionName: String?
)