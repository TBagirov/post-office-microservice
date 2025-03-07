package org.bagirov.postalservice.dto.request

import java.util.*

data class DistrictUpdateRequest(
    val id: UUID,
    val postmanId: UUID,
    val regionId: UUID
)