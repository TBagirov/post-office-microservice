package org.bagirov.publicationservice.config

import io.minio.MinioClient
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfig(
    @Value("\${minio.endpoint}") private val endpoint: String,
    @Value("\${minio.accessKey}") private val accessKey: String,
    @Value("\${minio.secretKey}") private val secretKey: String,
) {

    private val log = KotlinLogging.logger {}

    @Bean
    fun minioClient(): MinioClient {
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build()
    }

}
