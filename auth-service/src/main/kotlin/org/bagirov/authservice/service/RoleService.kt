package org.bagirov.authservice.service

import org.bagirov.authservice.dto.response.RoleResponse
import org.bagirov.authservice.entity.RoleEntity
import org.bagirov.authservice.repository.RoleRepository
import org.bagirov.authservice.utill.convertToResponseEventDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RoleService(
    private val roleRepository: RoleRepository
) {
    fun getById(id: UUID): RoleResponse =
        roleRepository.findById(id)
            .orElseThrow { NoSuchElementException("Role with ID ${id} not found") }
            .convertToResponseEventDto()

    fun getAll(): List<RoleResponse> =
        roleRepository.findAll().map { it.convertToResponseEventDto() }

    @Transactional
    fun save(roleName: String) =
        roleRepository.save(
            RoleEntity(name = roleName)
        ).convertToResponseEventDto()

    fun delete(id: UUID): RoleResponse {
        val existingRole = roleRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Role with ID ${id} not found") }

        roleRepository.delete(existingRole)

        return existingRole.convertToResponseEventDto()
    }
}