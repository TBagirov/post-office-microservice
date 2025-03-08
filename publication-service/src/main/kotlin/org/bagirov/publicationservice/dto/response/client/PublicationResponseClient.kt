package org.bagirov.publicationservice.dto.response.client

import java.math.BigDecimal
import java.util.UUID

data class PublicationResponseClient(
    val id : UUID,
    val price: BigDecimal
)
