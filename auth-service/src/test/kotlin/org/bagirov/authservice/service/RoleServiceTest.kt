package org.bagirov.authservice.service


import org.bagirov.authservice.entity.RoleEntity
import org.bagirov.authservice.props.Role
import org.bagirov.authservice.repository.RoleRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class RoleServiceTest {

    @Mock
    private lateinit var roleRepository: RoleRepository

    @InjectMocks
    private lateinit var roleService: RoleService

    private lateinit var testRole: RoleEntity

    @BeforeEach
    fun setup() {
        testRole = RoleEntity(
            id = UUID.randomUUID(),
            name = Role.ADMIN
        )
    }

    @Test
    fun `getById should return role response when role exists`() {
        `when`(roleRepository.findById(testRole.id!!)).thenReturn(Optional.of(testRole))

        val result = roleService.getById(testRole.id!!)

        assertNotNull(result)
        assertEquals(testRole.id, result.id)
        assertEquals(testRole.name, result.name)
    }

    @Test
    fun `getById should throw NoSuchElementException when role does not exist`() {
        `when`(roleRepository.findById(any(UUID::class.java))).thenReturn(Optional.empty())

        assertThrows(NoSuchElementException::class.java) {
            roleService.getById(UUID.randomUUID())
        }
    }

    @Test
    fun `save should persist role and return response`() {
        `when`(roleRepository.save(any(RoleEntity::class.java))).thenReturn(testRole)

        val result = roleService.save(Role.ADMIN)

        assertNotNull(result)
        assertEquals(Role.ADMIN, result.name)
    }
}