package org.bagirov.authservice.utill

import org.bagirov.authservice.dto.UserEventDto
import org.bagirov.authservice.dto.UserUpdatedEventDto
import org.bagirov.authservice.dto.response.RoleResponse
import org.bagirov.authservice.dto.response.UserResponse
import org.bagirov.authservice.dto.response.client.AuthUserResponseClient
import org.bagirov.authservice.entity.RoleEntity
import org.bagirov.authservice.entity.UserEntity

fun RoleEntity.convertToResponseEventDto() = RoleResponse(
    id = this.id!!,
    name = this.name
)

fun UserEntity.convertToResponseEventDto() = UserEventDto(
    id = this.id ?: throw IllegalStateException("User ID is null"),
    createdAt = this.createdAt,
    role = this.role.name
)

fun UserEntity.convertToResponseDto() = UserResponse(
    id = this.id!!,
    name = this.name,
    surname = this.surname,
    patronymic = this.patronymic,
    username = this.username,
    password = this.password,
    email = this.email,
    phone = this.phone,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    role = role.name
)

fun UserEntity.convertToEventDto() = UserUpdatedEventDto(
    userId = this.id!!,
    name = this.name,
    surname = this.surname,
    patronymic = this.patronymic,
    email = this.email,
    phone = this.phone,
    updatedAt = this.updatedAt,
    role = role.name
)



fun UserResponse.convertToResponseClientDto() = AuthUserResponseClient(
    userId = this.id,
    name = this.name,
    surname = this.surname,
    patronymic = this.patronymic,
    username = this.username,
    email = this.email
)

