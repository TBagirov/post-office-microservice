package org.bagirov.reportservice.dto.response

import java.util.*

data class ReportPublicationResponse (
    val publicationId: UUID,
    val index: String,
    val title: String,
    val author: String,
    val type: String,
    val price: Int,
    val countSubscriber: Int
)