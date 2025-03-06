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
        ensureBucketExists()
        setPublicAccess()
    }

    private fun ensureBucketExists() {
        try {
            val bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
                log.info { "MinIO Bucket '$bucketName' created" }
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

        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(fileName)
                .stream(file.inputStream, file.size, -1)
                .contentType(file.contentType)
                .build()
        )

        log.info { "Файл загружен: $fileName" }

        return "$endpoint/$bucketName/$fileName" // Формируем ПРАВИЛЬНЫЙ URL
    }


    fun deleteFile(fileUrl: String) {
        val fileName = fileUrl.substringAfterLast("/")
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .`object`(fileName)
                .build()
        )
        log.info { "File deleted: $fileName" }
    }

    private fun sanitizeFilename(filename: String): String {
        return filename.trim()
            .replace("\\s+".toRegex(), "_") // Заменяем пробелы на "_"
            .replace("[^a-zA-Z0-9._-]".toRegex(), "") // Оставляем только буквы, цифры, ".", "_", "-"
            .replace("__+".toRegex(), "_") // Убираем двойные подчеркивания
    }


    /**
     * Автоматическая очистка неиспользуемых файлов.
     * Запускается **каждый день в 03:00 ночи**.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    fun cleanUnusedFiles() {
        log.info { "Запуск очистки неиспользуемых файлов..." }

        try {
            val objects = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build()
            )

            for (result in objects) {
                val file = result.get()
                val fileName = file.objectName()

                // Проверяем, используется ли этот файл в БД
                val isFileUsed = publicationRepository.existsByCoverUrl(fileName)
                if (!isFileUsed) {
                    try {
                        minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .`object`(fileName)
                                .build()
                        )
                        log.info { "Удалён неиспользуемый файл: $fileName" }
                    } catch (e: Exception) {
                        log.error(e) { "Ошибка при удалении файла: $fileName" }
                    }
                }
            }
        } catch (e: Exception) {
            log.error(e) { "Ошибка при получении списка файлов из MinIO" }
        }

        log.info { "Очистка завершена!" }
    }

}
