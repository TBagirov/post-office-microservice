package org.bagirov.authservice.utill

import org.bagirov.authservice.dto.response.RoleResponse
import org.bagirov.authservice.entity.RoleEntity

fun RoleEntity.convertToResponseDto() = RoleResponse(
    id = this.id!!,
    name = this.name
)
