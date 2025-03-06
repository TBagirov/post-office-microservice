package org.bagirov.publicationservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class PublicationServiceApplication

fun main(args: Array<String>) {
	runApplication<PublicationServiceApplication>(*args)
}
