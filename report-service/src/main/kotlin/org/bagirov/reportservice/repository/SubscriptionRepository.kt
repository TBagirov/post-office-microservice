package org.bagirov.reportservice.repository

import org.bagirov.reportservice.dto.response.ReportSubscriptionByIdSubscriberResponse
import org.bagirov.reportservice.dto.response.ReportSubscriptionResponse
import org.bagirov.reportservice.entity.SubscriptionEntity
import org.bagirov.reportservice.props.SubscriptionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SubscriptionRepository : JpaRepository<SubscriptionEntity, UUID> {
    fun findByStatus(status: SubscriptionStatus): List<SubscriptionEntity>
    fun findBySubscriptionId(subscriptionId: UUID): SubscriptionEntity?

    @Query(nativeQuery = true, value = """
        SELECT 
            s.subscription_id, 
            p.publication_id, 
            p.title, 
            p.type, 
            s.start_date_subscription AS start_date, 
            s.end_date_subscription AS end_date, 
            p.price 
        FROM report_subscription s
        JOIN report_publication p ON s.publication_id = p.publication_id
        WHERE s.subscriber_id = :subscriberId
    """)
    fun getSubscriptionsBySubscriberId(@Param("subscriberId") subscriberId: UUID): List<Any>

    @Query(nativeQuery = true, value = """
        SELECT 
            s.subscription_id, 
            sub.subscriber_id, 
            p.publication_id, 
            CONCAT(sub.surname, ' ', sub.name, ' ', sub.patronymic) AS fio_subscriber, 
            p.title AS title_publication, 
            s.start_date_subscription, 
            s.end_date_subscription, 
            s.status AS status_subscription
        FROM report_subscription s
        JOIN report_subscriber sub ON s.subscriber_id = sub.subscriber_id
        JOIN report_publication p ON s.publication_id = p.publication_id
    """)
    fun getAllSubscriptionsReport(): List<Any>
}
