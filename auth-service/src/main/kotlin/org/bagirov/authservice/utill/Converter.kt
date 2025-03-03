package org.bagirov.authservice.utill

import org.bagirov.authservice.dto.UserEventResponse
import org.bagirov.authservice.dto.response.RoleResponse
import org.bagirov.authservice.entity.RoleEntity
import org.bagirov.authservice.entity.UserEntity

fun RoleEntity.convertToResponseDto() = RoleResponse(
    id = this.id!!,
    name = this.name
)

fun UserEntity.convertToResponseDto() = UserEventResponse(
    id = this.id!!,
//    name = this.name,
//    surname = this.surname,
//    patronymic = this.patronymic,
//    email = this.email,
//    phone = this.phone,
    createdAt = this.createdAt,
    role = this.role.name
)