package org.bagirov.authservice.controller

import mu.KotlinLogging
import org.bagirov.authservice.dto.response.RoleResponse
import org.bagirov.authservice.service.RoleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/role")
class RoleController(
    private val roleService: RoleService
) {
    private val log = KotlinLogging.logger {}

    @GetMapping("/{id}")
    fun getRole(@PathVariable id: UUID): ResponseEntity<RoleResponse> {
        log.info { "Request get Role by id: $id" }
        return ResponseEntity.ok(roleService.getById(id))
    }

    @GetMapping()
    fun getAll(): ResponseEntity<List<RoleResponse>>{
        log.info { "Request get All Role" }
        return ResponseEntity.ok(roleService.getAll())
    }


    @PostMapping()
    fun save(@RequestBody role: String): ResponseEntity<RoleResponse> {
        log.info { "Request save Role" }
        return ResponseEntity.ok(roleService.save(role))
    }


    @DeleteMapping()
    fun delete(@RequestParam id: UUID): ResponseEntity<RoleResponse> {
        log.info { "Request delete Role by id: $id" }
        return ResponseEntity.ok(roleService.delete(id))
    }

}