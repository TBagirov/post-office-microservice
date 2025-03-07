package org.bagirov.publicationservice.dto.response

import java.math.BigDecimal
import java.util.*

data class PublicationResponse (
    val id: UUID,
    val index: String,
    val title: String,
    val author: String,
    val description: String?,
    val coverUrl: String?,
    val publicationType: String,
    val price: BigDecimal
)