package org.bagirov.postalservice.dto.request

import java.util.*

class StreetUpdateRequest (
    val id: UUID,
    val name: String,
    val regionId: UUID
)