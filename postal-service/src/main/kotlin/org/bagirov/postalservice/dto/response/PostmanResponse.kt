package org.bagirov.postalservice.dto.response

import java.util.*

data class PostmanResponse(
    val id: UUID,
    val userId: UUID,
//    val surname: String,
//    val name: String,
//    val patronymic: String,
    val regions: List<String?>?
)