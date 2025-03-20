package org.bagirov.reportservice.service

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.bagirov.reportservice.dto.response.ReportPublicationResponse
import org.bagirov.reportservice.dto.response.ReportSubscriptionByIdSubscriberResponse
import org.bagirov.reportservice.dto.response.ReportSubscriptionResponse
import org.bagirov.reportservice.props.SubscriptionStatus
import org.bagirov.reportservice.repository.PublicationRepository
import org.bagirov.reportservice.repository.SubscriptionRepository
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class ReportService(
    private val publicationRepository: PublicationRepository,
    private val subscriptionRepository: SubscriptionRepository,
) {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy")

    fun subscriptionReport(): ByteArray? {
        val reportData = subscriptionRepository.getAllSubscriptionsReport().map { row ->
            val columns = row as Array<*>
            ReportSubscriptionResponse(
                subscriptionId = columns[0] as UUID,
                subscriberId = columns[1] as UUID,
                publicationId = columns[2] as UUID,
                fioSubscriber = columns[3] as String,
                titlePublication = columns[4] as String,
                startDateSubscription = (columns[5] as Timestamp).toLocalDateTime(),
                endDateSubscription = (columns[6] as Timestamp).toLocalDateTime(),
                statusSubscription = SubscriptionStatus.valueOf(columns[7] as String)
            )
        }

        val headers = listOf(
            "Id Подписки", "Id Подписчика", "Id Издания", "ФИО Подписчика",
            "Название издания", "Дата начала подписки", "Дата окончания подписки", "Статус Подписки"
        )

        return generateExcelReport("Report Subscription", headers, reportData) { row, report ->
            row.createCell(0).setCellValue(report.subscriptionId.toString())
            row.createCell(1).setCellValue(report.subscriberId.toString())
            row.createCell(2).setCellValue(report.publicationId.toString())
            row.createCell(3).setCellValue(report.fioSubscriber)
            row.createCell(4).setCellValue(report.titlePublication)
            row.createCell(5).setCellValue(report.startDateSubscription.format(dateFormatter))
            row.createCell(6).setCellValue(report.endDateSubscription.format(dateFormatter))
            row.createCell(7).setCellValue(report.statusSubscription.name)
        }
    }

    fun publicationReport(): ByteArray? {
        val reportData = publicationRepository.getReportPublications().map { row ->
            val columns = row as Array<*>
            ReportPublicationResponse(
                publicationId = columns[0] as UUID,
                index = columns[1] as String,
                title = columns[2] as String,
                author = columns[3] as String,
                type = columns[4] as String,
                price = (columns[5] as BigDecimal).toInt(),
                countSubscriber = columns[6] as Int
            )
        }

        val headers = listOf(
            "Id Издания", "Книжный индекс", "Название издания", "Тип издания", "Цена", "Количество подписчиков"
        )

        return generateExcelReport("Report Publication", headers, reportData) { row, report ->
            row.createCell(0).setCellValue(report.publicationId.toString())
            row.createCell(1).setCellValue(report.index)
            row.createCell(2).setCellValue(report.title)
            row.createCell(3).setCellValue(report.type)
            row.createCell(4).setCellValue(report.price.toDouble())
            row.createCell(5).setCellValue(report.countSubscriber.toDouble())
        }
    }

    fun subscriptionByIdSubscriberReport(subscriberId: UUID): ByteArray? {
        val reportData = subscriptionRepository.getSubscriptionsBySubscriberId(subscriberId).map { row ->
            val columns = row as Array<*>
            ReportSubscriptionByIdSubscriberResponse(
                subscriptionId = columns[0] as UUID,
                publicationId = columns[1] as UUID,
                title = columns[2] as String,
                type = columns[3] as String,
                startDate = (columns[4] as Timestamp).toLocalDateTime(),
                endDate = (columns[5] as Timestamp).toLocalDateTime(),
                price = columns[6] as BigDecimal
            )
        }

        val headers = listOf(
            "Id Подписки", "Id Издания", "Название издания", "Тип издания",
            "Цена", "Дата начала подписки", "Дата окончания подписки"
        )

        return generateExcelReport("Report Subscription by Subscriber", headers, reportData) { row, report ->
            row.createCell(0).setCellValue(report.subscriptionId.toString())
            row.createCell(1).setCellValue(report.publicationId.toString())
            row.createCell(2).setCellValue(report.title)
            row.createCell(3).setCellValue(report.type)
            row.createCell(4).setCellValue(report.price.toDouble())
            row.createCell(5).setCellValue(report.startDate.format(dateFormatter))
            row.createCell(6).setCellValue(report.endDate.format(dateFormatter))
        }
    }

    /**
     * Универсальный метод для генерации Excel-отчета.
     */
    private fun <T> generateExcelReport(
        sheetName: String,
        headers: List<String>,
        data: List<T>,
        fillRow: (Row, T) -> Unit
    ): ByteArray? {
        return try {
            XSSFWorkbook().use { workbook ->
                val sheet = workbook.createSheet(sheetName)
                createHeaderRow(sheet, headers, workbook)
                data.forEachIndexed { index, report ->
                    val row = sheet.createRow(index + 1)
                    fillRow(row, report)
                }
                ByteArrayOutputStream().use { outputStream ->
                    workbook.write(outputStream)
                    outputStream.toByteArray()
                }
            }
        } catch (e: Exception) {
            println("Ошибка генерации отчета: ${e.message}")
            null
        }
    }

    /**
     * Создает заголовок таблицы.
     */
    private fun createHeaderRow(sheet: Sheet, headers: List<String>, workbook: Workbook) {
        val headerStyle = workbook.createCellStyle().apply {
            val font = workbook.createFont().apply {
                bold = true
                fontHeightInPoints = 14.toShort()
            }
            setFont(font)
            wrapText = true
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index).apply {
                setCellValue(header)
                cellStyle = headerStyle
            }
            sheet.setColumnWidth(index, 6000)
        }
    }
}
