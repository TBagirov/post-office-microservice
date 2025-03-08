package org.bagirov.subscriptionservice.repository


import org.bagirov.subscriptionservice.entity.SubscriptionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SubscriptionRepository: JpaRepository<SubscriptionEntity, UUID> {
    fun findBySubscriberId(subscriberId: UUID): List<SubscriptionEntity>?

}