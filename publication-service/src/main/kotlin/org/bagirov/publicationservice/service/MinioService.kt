package org.bagirov.publicationservice.service

import io.minio.*
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.bagirov.publicationservice.repository.PublicationRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class MinioService(
    private val minioClient: MinioClient, // Теперь мы получаем MinioClient из конфигурации
    @Value("\${minio.endpoint}") private val endpoint: String,
    @Value("\${minio.bucket}") private val bucketName: String,

    private val publicationRepository: PublicationRepository
) {

    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun init() {
        log.info { "Initializing MinIO bucket settings..." }
        ensureBucketExists()
        setPublicAccess()
    }

    private fun ensureBucketExists() {
        try {
            val bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
                log.info { "MinIO Bucket '$bucketName' created" }
            } else {
                log.info { "MinIO Bucket '$bucketName' already exists" }
            }
        } catch (e: Exception) {
            log.error(e) { "Error checking/creating MinIO bucket: '$bucketName'" }
        }
    }

    private fun setPublicAccess() {
        val policyJson = """
        {
          "Version": "2012-10-17",
          "Statement": [
            {
              "Effect": "Allow",
              "Principal": "*",
              "Action": ["s3:GetObject"],
              "Resource": ["arn:aws:s3:::$bucketName/*"]
            }
          ]
        }
        """.trimIndent()

        minioClient.setBucketPolicy(
            SetBucketPolicyArgs.builder()
                .bucket(bucketName)
                .config(policyJson)
                .build()
        )

        log.info { "MinIO Bucket '$bucketName' is now public (Read-Only)." }
    }

    fun uploadFile(file: MultipartFile): String {
        val sanitizedFilename = sanitizeFilename(file.originalFilename ?: "file")
        val fileId = UUID.randomUUID().toString()
        val fileName = "$fileId-$sanitizedFilename"

        log.info { "Uploading file '$fileName' to MinIO bucket '$bucketName'..." }

        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(fileName)
                .stream(file.inputStream, file.size, -1)
                .contentType(file.contentType)
                .build()
        )

        log.info { "File uploaded successfully: $fileName" }
        return "$endpoint/$bucketName/$fileName"
    }

    fun deleteFile(fileUrl: String) {
        val fileName = fileUrl.substringAfterLast("/")
        log.info { "Deleting file '$fileName' from MinIO bucket '$bucketName'..." }

        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .`object`(fileName)
                .build()
        )

        log.info { "File deleted successfully: $fileName" }
    }

    private fun sanitizeFilename(filename: String): String {
        return filename.trim()
            .replace("\\s+".toRegex(), "_")
            .replace("[^a-zA-Z0-9._-]".toRegex(), "")
            .replace("__+".toRegex(), "_")
    }

    /**
     * Automatic cleanup of unused files.
     * Runs **every day at 03:00 AM**.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    fun cleanUnusedFiles() {
        log.info { "Starting cleanup of unused files..." }

        try {
            val objects = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build()
            )

            for (result in objects) {
                val file = result.get()
                val fileName = file.objectName()

                val isFileUsed = publicationRepository.existsByCoverUrl(fileName)
                if (!isFileUsed) {
                    try {
                        minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .`object`(fileName)
                                .build()
                        )
                        log.info { "Deleted unused file: $fileName" }
                    } catch (e: Exception) {
                        log.error(e) { "Error deleting file: $fileName" }
                    }
                }
            }
        } catch (e: Exception) {
            log.error(e) { "Error retrieving file list from MinIO" }
        }

        log.info { "Cleanup process completed!" }
    }

}
