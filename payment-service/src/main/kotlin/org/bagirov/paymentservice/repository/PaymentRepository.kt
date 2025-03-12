package org.bagirov.paymentservice.repository

import org.bagirov.paymentservice.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PaymentRepository : JpaRepository<PaymentEntity, UUID>
{
}