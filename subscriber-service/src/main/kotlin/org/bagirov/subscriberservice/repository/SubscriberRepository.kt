package org.bagirov.subscriberservice.repository


import org.bagirov.subscriberservice.entity.SubscriberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SubscriberRepository: JpaRepository<SubscriberEntity, UUID> {

    fun findByUserId(userId: UUID): SubscriberEntity?

}