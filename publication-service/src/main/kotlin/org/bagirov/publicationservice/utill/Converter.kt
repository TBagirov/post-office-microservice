package org.bagirov.publicationservice.utill

import org.bagirov.publicationservice.dto.PublicationReportEventDto
import org.bagirov.publicationservice.dto.request.PublicationTypeRequest
import org.bagirov.publicationservice.dto.request.update.PublicationUpdateEventDto
import org.bagirov.publicationservice.dto.request.update.PublicationUpdateRequest
import org.bagirov.publicationservice.dto.response.PublicationResponse
import org.bagirov.publicationservice.dto.response.PublicationTypeResponse
import org.bagirov.publicationservice.entity.PublicationEntity
import org.bagirov.publicationservice.entity.PublicationTypeEntity



fun PublicationEntity.convertToResponseDto() = PublicationResponse(
    id = this.id!!,
    index = this.index,
    author = this.author,
    description = this.description!!,
    coverUrl = this.coverUrl,
    title = this.title,
    publicationType = this.type.name,
    price = this.price
)
fun PublicationEntity.convertToEventDto() = PublicationReportEventDto(
    id = this.id!!,
    index = this.index,
    author = this.author,
    price = this.price,
    title = this.title,
    publicationType = this.type.name,
)
fun PublicationUpdateRequest.convertToEventDto() = PublicationUpdateEventDto(
    id= this.id,
    index = this.index,
    title = this.title,
    author = this.author,
    price = this.price,
    typeName = this.typeName
)

fun PublicationTypeEntity.convertToResponseDto() = PublicationTypeResponse(
    id = this.id!!,
    type = this.name,
    publications = this.publications?.map { it.convertToResponseDto() }
)
fun PublicationTypeRequest.convertToEntity() = PublicationTypeEntity(
    id = null,
    name = this.type,
    publications = null
)


