package org.bagirov.publicationservice.dto.request.update

import java.math.BigDecimal
import java.util.*


data class PublicationUpdateRequest(
    val id: UUID,
    val index: String?,
    val title: String?,
    val description: String?,
    val coverUrl: String?,
    val author: String?,
    val typeName: String?,
    val price: BigDecimal?
)