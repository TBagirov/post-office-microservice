package org.bagirov.authservice.config

import mu.KotlinLogging
import org.bagirov.authservice.entity.RoleEntity
import org.bagirov.authservice.entity.UserEntity
import org.bagirov.authservice.props.Role
import org.bagirov.authservice.repository.RoleRepository
import org.bagirov.authservice.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DataInitializer(
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    ) : CommandLineRunner {

    private val log = KotlinLogging.logger {}

    override fun run(vararg args: String?) {
        log.info { "Initializing default roles and admin user" }

        val roleAll = roleRepository.findAll()
        roleAll.find { it.name == Role.GUEST } ?: run {
            log.info { "Creating default role: GUEST" }
            roleRepository.save(RoleEntity(name = Role.GUEST))
        }
        roleAll.find { it.name == Role.SUBSCRIBER } ?: run {
            log.info { "Creating default role: SUBSCRIBER" }
            roleRepository.save(RoleEntity(name = Role.SUBSCRIBER))
        }
        roleAll.find { it.name == Role.POSTMAN } ?: run {
            log.info { "Creating default role: POSTMAN" }
            roleRepository.save(RoleEntity(name = Role.POSTMAN))
        }

        val userAll = userRepository.findAll()
        val adminExists = userAll.any { it.role.name == Role.ADMIN }

        if (!adminExists) {
            log.info { "Admin user not found, creating default admin" }
            val roleAdmin = roleAll.find { it.name == Role.ADMIN } ?: run {
                log.info { "Creating default role: ADMIN" }
                roleRepository.save(RoleEntity(name = Role.ADMIN))
            }

            val user = UserEntity(
                username = "admin",
                password = passwordEncoder.encode("admin"),
                role = roleAdmin,
                name = "adminName",
                surname = "adminSurname",
                patronymic = "adminPatronymic",
                email = "admin@example.com",
                phone = "+1234567890",
                createdAt = LocalDateTime.now()
            )
            userRepository.save(user)
            log.info { "Default admin user created successfully" }
        } else {
            log.info { "Admin user already exists, skipping creation" }
        }
    }
}
