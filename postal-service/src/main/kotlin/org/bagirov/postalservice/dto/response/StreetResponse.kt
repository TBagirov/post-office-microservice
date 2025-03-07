package org.bagirov.postalservice.dto.response

import java.util.*

data class StreetResponse(
    val id: UUID,
    val name: String,
    val regionName: String?
)
