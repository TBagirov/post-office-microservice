package org.bagirov.publicationservice.dto.request

import java.math.BigDecimal


data class PublicationRequest(
    val index: String,
    val title: String,
    val author: String,
    val description: String?,
    val type: String,
    val price: BigDecimal
)