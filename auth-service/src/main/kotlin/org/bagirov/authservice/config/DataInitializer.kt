package org.bagirov.authservice.config

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

    override fun run(vararg args: String?) {

        val roleAll = roleRepository.findAll()
        roleAll.find { it.name == Role.GUEST } ?: roleRepository.save(RoleEntity(name = Role.GUEST))
        roleAll.find { it.name == Role.SUBSCRIBER } ?: roleRepository.save(RoleEntity(name = Role.SUBSCRIBER))
        roleAll.find { it.name == Role.POSTMAN} ?: roleRepository.save(RoleEntity(name = Role.POSTMAN))


        val userAll = userRepository.findAll()
        val id = userAll.find { it.role.name == Role.ADMIN }
        if (id == null) {

            val roleAdmin = roleAll.find { it.name == Role.ADMIN } ?: roleRepository.save(RoleEntity(name = Role.ADMIN))

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

            println("Initial data has been inserted into the database.")
        }



        println("The user with the admin role already exists.")
    }
}
