package org.bagirov.authservice.dto.response

import org.bagirov.authservice.entity.UserEntity
import java.util.*


// TODO: не получается выводить юзеров, ругается что где-то параллельно происходит изменение коллекции юзеров
data class RoleResponse(
    val id: UUID,
    val name: String
)
