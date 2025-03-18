package org.bagirov.authservice.service

import mu.KotlinLogging
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
    private val log = KotlinLogging.logger {}

    fun getById(id: UUID): RoleResponse {
        log.info { "Fetching role by ID: $id" }
        return roleRepository.findById(id)
            .orElseThrow {
                log.error { "Role with ID $id not found" }
                NoSuchElementException("Role with ID $id not found")
            }
            .convertToResponseEventDto()
    }

    fun getAll(): List<RoleResponse> {
        log.info { "Fetching all roles" }
        return roleRepository.findAll().map { it.convertToResponseEventDto() }
    }

    @Transactional
    fun save(roleName: String): RoleResponse {
        log.info { "Saving new role: $roleName" }
        return roleRepository.save(
            RoleEntity(name = roleName)
        ).convertToResponseEventDto().also {
            log.info { "Role saved successfully: $roleName" }
        }
    }

    fun delete(id: UUID): RoleResponse {
        log.info { "Deleting role by ID: $id" }
        val existingRole = roleRepository.findById(id)
            .orElseThrow {
                log.error { "Role with ID $id not found" }
                IllegalArgumentException("Role with ID $id not found")
            }

        roleRepository.delete(existingRole)
        log.info { "Role deleted successfully: $id" }
        return existingRole.convertToResponseEventDto()
    }

}