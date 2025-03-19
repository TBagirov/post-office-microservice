package org.bagirov.reportservice.repository

import org.bagirov.reportservice.entity.SubscriptionEntity
import org.bagirov.reportservice.props.SubscriptionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SubscriptionRepository : JpaRepository<SubscriptionEntity, UUID> {
    fun findByStatus(status: SubscriptionStatus): List<SubscriptionEntity>

    fun findBySubscriptionId(subscriptionId: UUID): SubscriptionEntity?
}