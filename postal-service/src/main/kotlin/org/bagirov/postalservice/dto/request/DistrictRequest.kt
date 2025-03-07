package org.bagirov.postalservice.dto.request

import java.util.*

data class DistrictRequest(
    val postmanId: UUID,
    val regionId: UUID
)