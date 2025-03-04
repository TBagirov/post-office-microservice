package org.bagirov.authservice.utill

import org.bagirov.authservice.dto.UserEventDto
import org.bagirov.authservice.dto.response.RoleResponse
import org.bagirov.authservice.dto.response.UserResponse
import org.bagirov.authservice.entity.RoleEntity
import org.bagirov.authservice.entity.UserEntity

fun RoleEntity.convertToResponseEventDto() = RoleResponse(
    id = this.id!!,
    name = this.name
)

fun UserEntity.convertToResponseEventDto() = UserEventDto(
    id = this.id!!,
//    name = this.name,
//    surname = this.surname,
//    patronymic = this.patronymic,
//    email = this.email,
//    phone = this.phone,
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
    role = role.name
)