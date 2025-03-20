package org.bagirov.subscriptionservice.repository


import org.bagirov.subscriptionservice.entity.SubscriptionEntity
import org.bagirov.subscriptionservice.props.SubscriptionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface SubscriptionRepository: JpaRepository<SubscriptionEntity, UUID> {
    fun findBySubscriberId(subscriberId: UUID): List<SubscriptionEntity>?

    fun findByStatusAndStartDateBefore(status: SubscriptionStatus, startDate: LocalDateTime): List<SubscriptionEntity>?

    fun countByPublicationIdAndStatus(publicationId: UUID, status: SubscriptionStatus): Long?
}