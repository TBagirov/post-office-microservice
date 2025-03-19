package org.bagirov.reportservice.service

import org.bagirov.reportservice.dto.response.ReportSubscriptionByIdSubscriberResponse
import org.bagirov.reportservice.dto.response.ReportSubscriptionResponse
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.util.*
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bagirov.reportservice.dto.response.ReportPublicationResponse
import org.bagirov.reportservice.props.SubscriptionStatus
import org.bagirov.reportservice.repository.PublicationRepository
import org.bagirov.reportservice.repository.SubscriberRepository
import org.bagirov.reportservice.repository.SubscriptionRepository
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime

@Service
class ReportService(
    private val publicationRepository: PublicationRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriberRepository: SubscriberRepository,
) {
    fun subscriptionReport(): ByteArray? {

        val report = subscriptionRepository.getAllSubscriptionsReport()
        val reportResponse = report.map { row ->
            val columns = row as Array<Any>
            ReportSubscriptionResponse(
                subscriptionId = columns[0] as UUID,
                subscriberId = columns[1] as UUID,
                publicationId = columns[2] as UUID,
                fioSubscriber = columns[3] as String,
                titlePublication = columns[4] as String,
                startDateSubscription = (columns[5] as Timestamp).toLocalDateTime(), // Исправлено
                endDateSubscription = (columns[6] as Timestamp).toLocalDateTime(),  // Исправлено
                statusSubscription = SubscriptionStatus.valueOf(columns[7] as String)
            )
        }


        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Report Subscription")


        // Устанавливаем ширину столбца
        sheet.setColumnWidth(0, 300 * 256 / 7)
        sheet.setColumnWidth(1, 300 * 256 / 7)
        sheet.setColumnWidth(2, 300 * 256 / 7)
        sheet.setColumnWidth(3, 200 * 256 / 7)
        sheet.setColumnWidth(4, 200 * 256 / 7)
        sheet.setColumnWidth(5, 200 * 256 / 7)
        sheet.setColumnWidth(6, 200 * 256 / 7)
        sheet.setColumnWidth(7, 200 * 256 / 7)
        sheet.setColumnWidth(8, 200 * 256 / 7)


        // Создание стиля для заголовков
        val headerStyle: CellStyle = workbook.createCellStyle()
        val headerFont: Font = workbook.createFont()
        headerFont.setBold(true)
        headerFont.setFontHeightInPoints((12 + 2).toShort()) // Увеличиваем размер шрифта на 2 пункта
        headerStyle.setFont(headerFont)
        headerStyle.setWrapText(true) // Включаем автоперенос текста


        // Добавляем границы к заголовку
        headerStyle.setBorderTop(BorderStyle.THIN)
        headerStyle.setBorderBottom(BorderStyle.THIN)
        headerStyle.setBorderLeft(BorderStyle.THIN)
        headerStyle.setBorderRight(BorderStyle.THIN)

        val headerRow: Row = sheet.createRow(0)
        val headerCell: Cell = headerRow.createCell(0)
        headerCell.setCellValue("Id Подписки")
        headerCell.setCellStyle(headerStyle)
        headerRow.createCell(1).setCellValue("Id Подписчика")
        headerRow.getCell(1).setCellStyle(headerStyle)
        headerRow.createCell(2).setCellValue("Id Издания")
        headerRow.getCell(2).setCellStyle(headerStyle)
        headerRow.createCell(3).setCellValue("ФИО Подписчика")
        headerRow.getCell(3).setCellStyle(headerStyle)
        headerRow.createCell(4).setCellValue("Название издания")
        headerRow.getCell(4).setCellStyle(headerStyle)
        headerRow.createCell(5).setCellValue("Дата начала подписки")
        headerRow.getCell(5).setCellStyle(headerStyle)
        headerRow.createCell(6).setCellValue("Дата окончания подписки")
        headerRow.getCell(6).setCellStyle(headerStyle)
        headerRow.createCell(7).setCellValue("Статус Подписки")
        headerRow.getCell(7).setCellStyle(headerStyle)


        // Создаем стиль с границами и автопереносом для данных
        val dataStyle: CellStyle = workbook.createCellStyle()
        dataStyle.setBorderTop(BorderStyle.THIN)
        dataStyle.setBorderBottom(BorderStyle.THIN)
        dataStyle.setBorderLeft(BorderStyle.THIN)
        dataStyle.setBorderRight(BorderStyle.THIN)
        dataStyle.setWrapText(true) // Включаем автоперенос текста

        var rowCount = 1
        val sb = StringBuffer()
        for (report in reportResponse) {
            sb.setLength(0)

            val row: Row = sheet.createRow(rowCount++)
            val subscriptionIdCell: Cell = row.createCell(0)
            subscriptionIdCell.setCellValue(report.subscriptionId.toString())
            subscriptionIdCell.setCellStyle(dataStyle)

            val subscriberIdCell: Cell = row.createCell(1)
            subscriberIdCell.setCellValue(report.subscriberId.toString())
            subscriberIdCell.setCellStyle(dataStyle)

            val publicationIdCell: Cell = row.createCell(2)
            publicationIdCell.setCellValue(report.publicationId.toString())
            publicationIdCell.setCellStyle(dataStyle)

            val fioSubscriberCell: Cell = row.createCell(3)
            fioSubscriberCell.setCellValue(report.fioSubscriber)
            fioSubscriberCell.setCellStyle(dataStyle)

            val titlePublicationCell: Cell = row.createCell(4)
            titlePublicationCell.setCellValue(report.titlePublication)
            titlePublicationCell.setCellStyle(dataStyle)

            val startDateSubscriptionCell: Cell = row.createCell(5)
            startDateSubscriptionCell.setCellValue(report.startDateSubscription)
            startDateSubscriptionCell.setCellStyle(dataStyle)

            val endDateSubscriptionCell: Cell = row.createCell(6)
            endDateSubscriptionCell.setCellValue(report.endDateSubscription)
            endDateSubscriptionCell.setCellStyle(dataStyle)

            val statusSubscriptionCell: Cell = row.createCell(7)
            statusSubscriptionCell.setCellValue(report.statusSubscription.name)
            statusSubscriptionCell.setCellStyle(dataStyle)

        }
        try {
            ByteArrayOutputStream().use { outputStream ->
                workbook.write(outputStream)
                workbook.close()
                return outputStream.toByteArray()
            }
        } catch (e: Exception) {
            print(e.message)
        }
        return null
    }

    fun publicationReport(): ByteArray? {

        val report = publicationRepository.getReportPublications()
        val reportResponse = report.map { row ->
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

        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Report Subscription")


        // Устанавливаем ширину столбца
        sheet.setColumnWidth(0, 300 * 256 / 7)
        sheet.setColumnWidth(1, 200 * 256 / 7)
        sheet.setColumnWidth(2, 200 * 256 / 7)
        sheet.setColumnWidth(3, 200 * 256 / 7)
        sheet.setColumnWidth(4, 200 * 256 / 7)


        // Создание стиля для заголовков
        val headerStyle: CellStyle = workbook.createCellStyle()
        val headerFont: Font = workbook.createFont()
        headerFont.setBold(true)
        headerFont.setFontHeightInPoints((12 + 2).toShort()) // Увеличиваем размер шрифта на 2 пункта
        headerStyle.setFont(headerFont)
        headerStyle.setWrapText(true) // Включаем автоперенос текста


        // Добавляем границы к заголовку
        headerStyle.setBorderTop(BorderStyle.THIN)
        headerStyle.setBorderBottom(BorderStyle.THIN)
        headerStyle.setBorderLeft(BorderStyle.THIN)
        headerStyle.setBorderRight(BorderStyle.THIN)

        val headerRow: Row = sheet.createRow(0)
        val headerCell: Cell = headerRow.createCell(0)
        headerCell.setCellValue("Id Издания")
        headerCell.setCellStyle(headerStyle)
        headerRow.createCell(1).setCellValue("Название издания")
        headerRow.getCell(1).setCellStyle(headerStyle)
        headerRow.createCell(2).setCellValue("Тип издания")
        headerRow.getCell(2).setCellStyle(headerStyle)
        headerRow.createCell(3).setCellValue("Цена")
        headerRow.getCell(3).setCellStyle(headerStyle)
        headerRow.createCell(4).setCellValue("Количество подписчиков")
        headerRow.getCell(4).setCellStyle(headerStyle)


        // Создаем стиль с границами и автопереносом для данных
        val dataStyle: CellStyle = workbook.createCellStyle()
        dataStyle.setBorderTop(BorderStyle.THIN)
        dataStyle.setBorderBottom(BorderStyle.THIN)
        dataStyle.setBorderLeft(BorderStyle.THIN)
        dataStyle.setBorderRight(BorderStyle.THIN)
        dataStyle.setWrapText(true) // Включаем автоперенос текста

        var rowCount = 1
        val sb = StringBuffer()
        for (report in reportResponse) {
            sb.setLength(0)

            val row: Row = sheet.createRow(rowCount++)
            val publicationIdCell: Cell = row.createCell(0)
            publicationIdCell.setCellValue(report.publicationId.toString())
            publicationIdCell.setCellStyle(dataStyle)

            val titleCell: Cell = row.createCell(1)
            titleCell.setCellValue(report.title)
            titleCell.setCellStyle(dataStyle)

            val typeCell: Cell = row.createCell(2)
            typeCell.setCellValue(report.type)
            typeCell.setCellStyle(dataStyle)

            val priceCell: Cell = row.createCell(3)
            priceCell.setCellValue(report.price.toDouble())
            priceCell.setCellStyle(dataStyle)

            val countSubscriberCell: Cell = row.createCell(4)
            countSubscriberCell.setCellValue(report.countSubscriber.toDouble())
            countSubscriberCell.setCellStyle(dataStyle)
        }

        try {
            ByteArrayOutputStream().use { outputStream ->
                workbook.write(outputStream)
                workbook.close()
                return outputStream.toByteArray()
            }
        } catch (e: Exception) {
            print(e.message)
        }
        return null

    }


    fun subscriptionByIdSubscriberReport(subscriberId: UUID): ByteArray? {

        val report = subscriptionRepository.getSubscriptionsBySubscriberId(subscriberId)
        val reportResponse = report.map { row ->
            val columns = row as Array<*>
            ReportSubscriptionByIdSubscriberResponse(
                subscriptionId = columns[0] as UUID,
                publicationId = columns[1] as UUID,
                title = columns[2] as String,
                type = columns[3] as String,
                startDate = columns[4] as LocalDateTime,
                endDate = columns[5] as LocalDateTime,
                price = columns[6] as BigDecimal
            )
        }

        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Report Subscription")


        // Устанавливаем ширину столбца
        sheet.setColumnWidth(0, 300 * 256 / 7)
        sheet.setColumnWidth(1, 300 * 256 / 7)
        sheet.setColumnWidth(2, 200 * 256 / 7)
        sheet.setColumnWidth(3, 200 * 256 / 7)
        sheet.setColumnWidth(4, 200 * 256 / 7)
        sheet.setColumnWidth(5, 200 * 256 / 7)
        sheet.setColumnWidth(6, 200 * 256 / 7)


        // Создание стиля для заголовков
        val headerStyle: CellStyle = workbook.createCellStyle()
        val headerFont: Font = workbook.createFont()
        headerFont.setBold(true)
        headerFont.setFontHeightInPoints((12 + 2).toShort()) // Увеличиваем размер шрифта на 2 пункта
        headerStyle.setFont(headerFont)
        headerStyle.setWrapText(true) // Включаем автоперенос текста


        // Добавляем границы к заголовку
        headerStyle.setBorderTop(BorderStyle.THIN)
        headerStyle.setBorderBottom(BorderStyle.THIN)
        headerStyle.setBorderLeft(BorderStyle.THIN)
        headerStyle.setBorderRight(BorderStyle.THIN)

        val headerRow: Row = sheet.createRow(0)
        val headerCell: Cell = headerRow.createCell(0)
        headerCell.setCellValue("Id Подписки")
        headerCell.setCellStyle(headerStyle)
        headerRow.createCell(1).setCellValue("Id Издания")
        headerRow.getCell(1).setCellStyle(headerStyle)
        headerRow.createCell(2).setCellValue("Название издания")
        headerRow.getCell(2).setCellStyle(headerStyle)
        headerRow.createCell(3).setCellValue("Тип издания")
        headerRow.getCell(3).setCellStyle(headerStyle)
        headerRow.createCell(4).setCellValue("Цена")
        headerRow.getCell(4).setCellStyle(headerStyle)
        headerRow.createCell(5).setCellValue("Дата начала подписки")
        headerRow.getCell(5).setCellStyle(headerStyle)
        headerRow.createCell(6).setCellValue("Дата окончания подписки")
        headerRow.getCell(6).setCellStyle(headerStyle)



        // Создаем стиль с границами и автопереносом для данных
        val dataStyle: CellStyle = workbook.createCellStyle()
        dataStyle.setBorderTop(BorderStyle.THIN)
        dataStyle.setBorderBottom(BorderStyle.THIN)
        dataStyle.setBorderLeft(BorderStyle.THIN)
        dataStyle.setBorderRight(BorderStyle.THIN)
        dataStyle.setWrapText(true) // Включаем автоперенос текста

        var rowCount = 1
        val sb = StringBuffer()
        for (report in reportResponse) {
            sb.setLength(0)

            val row: Row = sheet.createRow(rowCount++)
            val subscriptionIdCell: Cell = row.createCell(0)
            subscriptionIdCell.setCellValue(report.subscriptionId.toString())
            subscriptionIdCell.setCellStyle(dataStyle)

            val publicationIdCell: Cell = row.createCell(1)
            publicationIdCell.setCellValue(report.publicationId.toString())
            publicationIdCell.setCellStyle(dataStyle)

            val titleCell: Cell = row.createCell(2)
            titleCell.setCellValue(report.title)
            titleCell.setCellStyle(dataStyle)

            val typeCell: Cell = row.createCell(3)
            typeCell.setCellValue(report.type)
            typeCell.setCellStyle(dataStyle)

            val priceCell: Cell = row.createCell(4)
            priceCell.setCellValue(report.price!!.toDouble()  )
            priceCell.setCellStyle(dataStyle)

            val startDateCell: Cell = row.createCell(5)
            startDateCell.setCellValue(report.startDate)
            startDateCell.setCellStyle(dataStyle)

            val endDateCell: Cell = row.createCell(6)
            endDateCell.setCellValue(report.endDate)
            endDateCell.setCellStyle(dataStyle)

        }

        try {
            ByteArrayOutputStream().use { outputStream ->
                workbook.write(outputStream)
                workbook.close()
                return outputStream.toByteArray()
            }
        } catch (e: Exception) {
            print(e.message)
        }
        return null
    }

}