package org.bagirov.publicationservice.utill

import org.bagirov.publicationservice.dto.request.PublicationTypeRequest
import org.bagirov.publicationservice.dto.response.PublicationResponse
import org.bagirov.publicationservice.dto.response.PublicationTypeResponse
import org.bagirov.publicationservice.entity.PublicationEntity
import org.bagirov.publicationservice.entity.PublicationTypeEntity


fun PublicationTypeRequest.convertToEntity() = PublicationTypeEntity(
    id = null,
    name = this.type,
    publications = null
)

fun PublicationEntity.convertToResponseDto() = PublicationResponse(
    id = this.id!!,
    index = this.index,
    author = this.author,
    description = this.description!!,
    title = this.title,
    publicationType = this.type.name,
    price = this.price
)

fun PublicationTypeEntity.convertToResponseDto() = PublicationTypeResponse(
    id = this.id!!,
    type = this.name,
    publications = this.publications?.map { it.convertToResponseDto() }
)