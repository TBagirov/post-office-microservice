package org.bagirov.subscriptionservice.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaTopicConfig {

    @Bean
    fun subscriptionCreatedTopic(): NewTopic {
        return NewTopic("subscription-created", 1, 1.toShort())
    }

    @Bean
    fun paymentSuccessTopic(): NewTopic {
        return NewTopic("payment-success", 1, 1.toShort())
    }

    @Bean
    fun paymentFailedTopic(): NewTopic {
        return NewTopic("payment-failed", 1, 1.toShort())
    }
}