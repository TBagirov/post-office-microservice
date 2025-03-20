package org.bagirov.reportservice.repository

import org.bagirov.reportservice.entity.SubscriberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SubscriberRepository : JpaRepository<SubscriberEntity, UUID> {

    fun findByUserId(userId: UUID): SubscriberEntity?

    fun findBySubscriberId(subscriberId: UUID): SubscriberEntity?
}