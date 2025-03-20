package org.bagirov.reportservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging
import org.bagirov.publicationservice.dto.request.update.PublicationUpdateEventDto
import org.bagirov.reportservice.client.AuthServiceClient
import org.bagirov.reportservice.dto.*
import org.bagirov.reportservice.entity.PublicationEntity
import org.bagirov.reportservice.entity.SubscriberEntity
import org.bagirov.reportservice.entity.SubscriptionEntity
import org.bagirov.reportservice.props.Role
import org.bagirov.reportservice.repository.PublicationRepository
import org.bagirov.reportservice.repository.SubscriberRepository
import org.bagirov.reportservice.repository.SubscriptionRepository
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.util.*


@Service
class KafkaConsumerService (
    private val publicationRepository: PublicationRepository,
    private val subscriberRepository: SubscriberRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val objectMapper: ObjectMapper,
    private val authServiceClient: AuthServiceClient
) {

    private val log = KotlinLogging.logger {}

    @KafkaListener(topics = ["publication-created-events"], groupId = "report-service-group")
    fun consumePublicationCreatedEvent(message: String) {
        try {
            val publicationEvent = objectMapper.readValue(message, PublicationReportEventDto::class.java)

            val publication = PublicationEntity(
                publicationId = publicationEvent.id,
                index = publicationEvent.index,
                title = publicationEvent.title,
                author = publicationEvent.author,
                type = publicationEvent.publicationType,
                price = publicationEvent.price
            )

            publicationRepository.save(publication)
            log.info { "Publication add to report with id: ${publicationEvent.id}" }
        } catch (e: Exception) {
            log.error(e) { "Error processing Kafka message: ${e.message}" }
        }
    }

    @KafkaListener(topics = ["publication-deleted-events"], groupId = "report-service-group")
    fun consumePublicationDeletedEvent(message: String) {
        try {
            val event = objectMapper.readValue<Map<String, String>>(message)
            val publicationId =
                UUID.fromString(event["id"] ?: throw IllegalArgumentException("publicationId is missing"))

            publicationRepository.findByPublicationId(publicationId)?.let { publication ->
                publicationRepository.delete(publication)
                log.info { "Publication deleted for ID: $publicationId" }
            } ?: log.warn { "Publication with id $publicationId not found" }

        } catch (e: Exception) {
            log.error(e) { "Error processing Kafka message for user deletion: ${e.message}" }
        }
    }

    @KafkaListener(topics = ["publication-updated-events"], groupId = "report-service-group")
    fun consumePublicationUpdatedEvent(message: String) {
        try {
            val event = objectMapper.readValue(message, PublicationUpdateEventDto::class.java)

            publicationRepository.findByPublicationId(event.id)?.let { publication ->
                publication.apply {
                    event.index?.let { index = it }
                    event.title?.let { title = it }
                    event.author?.let { author = it }
                    event.price?.let { price = it }
                    event.typeName?.let { type = it }
                }
                publicationRepository.save(publication)
                log.info { "Publication updated for user ID: ${event.id}" }
            } ?: log.warn { "Publication with id ${event.id} not found" }

        } catch (e: Exception) {
            log.error(e) { "Error processing Kafka message for postman update: ${e.message}" }
        }
    }



    @KafkaListener(topics = ["subscriber-created-events"], groupId = "report-service-group")
    fun consumeSubscriberCreatedEvent(message: String) {
        try {
            val subscriberEvent = objectMapper.readValue(message, SubscriberEventDto::class.java)

            // Получаем email и username из AuthService
            val userDetails = authServiceClient.getUserDetails(subscriberEvent.userId)

            val subscriber = SubscriberEntity(
                subscriberId = subscriberEvent.subscriberId,
                username = userDetails.username,
                surname = userDetails.surname,
                name = userDetails.name,
                patronymic = userDetails.patronymic,
                userId = userDetails.userId
            )

            subscriberRepository.save(subscriber)

            log.info { "Subscriber add to report with id: ${subscriberEvent.subscriberId}" }
        } catch (e: Exception) {
            log.error(e) { "Error processing Kafka message: ${e.message}" }
        }
    }

    @KafkaListener(topics = ["user-updated-events"], groupId = "report-service-group")
    fun consumeUserUpdatedEvent(message: String) {
        try {
            val event = objectMapper.readValue(message, UserUpdatedEventDto::class.java)

            if(event.role == Role.SUBSCRIBER)
                subscriberRepository.findByUserId(event.userId)?.let { subscriber ->

                    subscriber.apply {
                        name = event.name
                        surname = event.surname
                        patronymic = event.patronymic
                    }

                    subscriberRepository.save(subscriber)
                    log.info { "Subscriber updated for user ID: ${event.userId}" }
                } ?: log.warn { "Subscriber with userId ${event.userId} not found" }

        } catch (e: Exception) {
            log.error(e) { "Error processing Kafka message for postman update: ${e.message}" }
        }
    }

    @KafkaListener(topics = ["user-deleted-events"], groupId = "report-service-group")
    fun consumeUserDeletedEvent(message: String) {
        try {
            val event = objectMapper.readValue<Map<String, String>>(message)
            val userId = UUID.fromString(event["id"] ?: throw IllegalArgumentException("userId is missing"))

            subscriberRepository.findByUserId(userId)?.let { subscriber ->
                subscriberRepository.delete(subscriber)
                log.info {"Subscriber deleted for user $userId"}
            } ?: log.warn {"Subscriber with userId $userId not found"}

        } catch (e: Exception) {
            log.error(e) { "Error processing Kafka message about user deletion: ${e.message}" }
        }
    }


    @KafkaListener(topics = ["subscription-created-events"], groupId = "report-service-group")
    fun handleSubscriptionEvent(message: String) {
        try {
            val event = objectMapper.readValue(message, SubscriptionCreatedEvent::class.java) // Десериализуем JSON в DTO

            val publication = publicationRepository.findByPublicationId(event.publicationId)
                ?: throw NoSuchElementException("Publication with ID ${event.publicationId} not found")

            val subscriber = subscriberRepository.findBySubscriberId(event.subscriberId)
                ?: throw NoSuchElementException("Subscriber with ID ${event.subscriberId} not found")

            val subscription = SubscriptionEntity(
                subscriptionId = event.subscriptionId,
                startDateSubscription = event.startDate,
                endDateSubscription = event.endDate,
                status = event.status,
                publication = publication,
                subscriber = subscriber,
            )

            subscriptionRepository.save(subscription)
            log.info {"Processed subscription-created event: $event"}

            publication.countSubscriber++
            publicationRepository.save(publication)
            log.info {"add count subscriber in publication with index: ${publication.index}"}
        } catch (e: Exception) {
            log.error(e) { "Error processing subscription-created event: ${e.message}" }
        }
    }

    @KafkaListener(topics = ["subscription-updated-events"], groupId = "report-service-group")
    fun consumeSubscriptionUpdatedEvent(message: String) {
        try {
            val event = objectMapper.readValue(message, SubscriptionUpdatedEvent::class.java)

            val subscription = subscriptionRepository.findBySubscriptionId(event.subscriptionId)
               ?: throw NoSuchElementException("Subscription with ID ${event.subscriptionId} not found")

            subscription.status = event.newStatus
            subscriptionRepository.save(subscription)

        } catch (e: Exception) {
            log.error(e) { "Error processing Kafka message for postman update: ${e.message}" }
        }
    }

    @KafkaListener(topics = ["subscription-deleted-events"], groupId = "report-service-group")
    fun consumeSubscriptionDeletedEvent(message: String) {
        try {
            val event = objectMapper.readValue<Map<String, String>>(message)
            val subscriptionId = UUID.fromString(event["id"] ?: throw IllegalArgumentException("subscriptionId is missing"))

            subscriptionRepository.findBySubscriptionId(subscriptionId)?.let { subscription ->
                subscriptionRepository.delete(subscription)
                log.info {"Subscription deleted for subscriptionId $subscriptionId"}
            } ?: log.warn {"Subscription with subscriptionId $subscriptionId not found"}

        } catch (e: Exception) {
            log.error(e) { "Error processing Kafka message about subscription deletion: ${e.message}" }
        }
    }


}