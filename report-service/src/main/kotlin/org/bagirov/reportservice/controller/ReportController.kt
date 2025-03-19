package org.bagirov.reportservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.bagirov.reportservice.service.ReportService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@CrossOrigin(origins = arrayOf("http://localhost:3000"))
@RestController
@RequestMapping("/api/report")
@Tag(name = "ReportController", description = "Контроллер для взаимодействия с отчетами")
class ReportController (
    private val reportService: ReportService
){
    private val log = KotlinLogging.logger {}

    @GetMapping("/subscriptions")
    @Operation(
        summary = "Получение отчета по подпискам",
        description = "Получение отчета с данными о подписках"
    )
    fun getReportSubscriptions(): ResponseEntity<ByteArray> {
        log.info { "Request get Report on Subscriptions" }

        val excelFile: ByteArray = reportService.subscriptionReport()
            ?: throw Exception("Failed to create Excel file ReportSubscriptions")

        val headers = generateHeadersFile()
        return ResponseEntity<ByteArray>(excelFile, headers, HttpStatus.OK)
    }

    @GetMapping("/publications")
    @Operation(
        summary = "Получение отчета по изданиям",
        description = "Получение отчета с данными о издания"
    )
    fun getReportPublications(): ResponseEntity<ByteArray> {
        log.info { "Request get Report on Publications" }

        val excelFile: ByteArray = reportService.publicationReport()
            ?: throw Exception("Failed to create Excel file ReportPublications")

        val headers = generateHeadersFile()
        return ResponseEntity<ByteArray>(excelFile, headers, HttpStatus.OK)
    }

    @GetMapping("/subscriber-subscriptions")
    @Operation(
        summary = "Получение отчета о подписках подписчика по id",
        description = "Получение отчета c данными о подписках подписчика по id"
    )
    fun getReportSubscriptionsBySubscriberId(@RequestParam id: UUID): ResponseEntity<ByteArray> {
        log.info { "Request get Report Subscription by Subscriber id: $id" }

        val excelFile: ByteArray = reportService.subscriptionByIdSubscriberReport(id)
            ?: throw Exception("Failed to create Excel file ReportSubscriptionsBySubscriberId")

        val headers = generateHeadersFile()
        return ResponseEntity<ByteArray>(excelFile, headers, HttpStatus.OK)
    }

    private fun generateHeadersFile(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM

        val nameFile = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd__HH.mm"))
        val typeFile = ".xlsx"

        headers.setContentDispositionFormData("attachment", nameFile + typeFile)
        return headers
    }
}