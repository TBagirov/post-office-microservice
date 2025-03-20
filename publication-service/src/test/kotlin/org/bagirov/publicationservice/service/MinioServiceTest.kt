package org.bagirov.publicationservice.service


import io.minio.MinioClient
import io.minio.ObjectWriteResponse
import io.mockk.*
import org.bagirov.publicationservice.repository.PublicationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.multipart.MultipartFile
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MinioServiceTest {

    private val minioClient: MinioClient = mockk()
    private val publicationRepository: PublicationRepository = mockk()
    private lateinit var minioService: MinioService

    private val bucketName = "book-covers"
    private val endpoint = "http://minio:9000"

    @BeforeEach
    fun setUp() {
        minioService = MinioService(minioClient, endpoint, bucketName, publicationRepository)
    }

    @Test
    fun `should upload file and return URL`() {
        val file: MultipartFile = mockk()
        val fileName = "test-file.jpg"

        every { file.originalFilename } returns fileName
        every { file.inputStream } returns mockk()
        every { file.size } returns 1024L
        every { file.contentType } returns "image/jpeg"
        every { minioClient.putObject(any()) } returns mockk<ObjectWriteResponse>()

        val result = minioService.uploadFile(file)

        assertNotNull(result)
        assert(result.startsWith(endpoint)) { "URL должен начинаться с $endpoint, но получили $result" }
    }

    @Test
    fun `should delete file`() {
        val fileUrl = "$endpoint/$bucketName/test-file.jpg"
        val fileName = "test-file.jpg"

        every { minioClient.removeObject(any()) } just Runs

        minioService.deleteFile(fileUrl)

        verify {
            minioClient.removeObject(
                withArg {
                    assertEquals(fileName, it.`object`())
                }
            )
        }
    }

    @Test
    fun `should handle exception during file deletion`() {
        val fileUrl = "$endpoint/$bucketName/test-file.jpg"
        every { minioClient.removeObject(any()) } throws RuntimeException("Delete error")

        assertThrows<RuntimeException> {
            minioService.deleteFile(fileUrl)
        }
    }
}
