package org.bagirov.publicationservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PublicationServiceApplication

fun main(args: Array<String>) {
	runApplication<PublicationServiceApplication>(*args)
}
