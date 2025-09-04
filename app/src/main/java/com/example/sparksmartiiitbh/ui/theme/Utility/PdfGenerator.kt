package com.example.sparksmartiiitbh.ui.theme.Utility
import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.text.SimpleDateFormat
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.sparksmartiiitbh.ui.theme.Model.Complaint

class PdfGenerator(private val context: Context) {

    fun generateFuelLogsPdf(
        logs: List<String>,
        startDate: String,
        endDate: String,
        onComplete: (File?) -> Unit
    ) {
        try {
            val fileName = "Fuel_Logs_${startDate}_to_${endDate}.pdf"
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            val outputStream = FileOutputStream(file)

            val writer = PdfWriter(outputStream)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)


            val title = Paragraph("Fuel Logs Report")
                .setBold()
                .setFontSize(18f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(title)


            val dateRange = Paragraph("From $startDate to $endDate")
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
            document.add(dateRange)

            val table = Table(UnitValue.createPercentArray(floatArrayOf(5f, 30f, 30f, 35f)))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setWidth(UnitValue.createPercentValue(100f))

            addTableHeader(table, listOf("#", "Time", "Fuel Change", "Remaining"))

            logs.forEachIndexed { index, log ->
                val parts = log.split("|").map { part ->
                    part.replace("Time:", "")
                        .replace("Remaining:", "")
                        .trim()
                }

                if (parts.size >= 3) {

                    table.addCell(Cell().add(Paragraph("${index + 1})").setTextAlignment(TextAlignment.LEFT)))


                    table.addCell(Cell().add(Paragraph(parts[0]).setTextAlignment(TextAlignment.LEFT)))


                    val fuelChange = parts[1]
                    val fuelCell = Cell().add(Paragraph(fuelChange).setTextAlignment(TextAlignment.LEFT))
                    if (fuelChange.contains("added")) {
                        fuelCell.setBackgroundColor(ColorConstants.GREEN)
                    } else {
                        fuelCell.setBackgroundColor(ColorConstants.RED)
                    }
                    table.addCell(fuelCell)

                    table.addCell(Cell().add(Paragraph(parts[2]).setTextAlignment(TextAlignment.LEFT)))
                }
            }

            document.add(table)
            document.close()

            onComplete(file)
            openPdf(file)

        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(null)
        }
    }

    fun generateComplaintsPdf(
        complaints: List<Complaint>,
        titleText: String = "Complaints Report",
        onComplete: (File?) -> Unit
    ) {
        try {
            val time = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
            val fileName = "Complaints_$time.pdf"
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            val outputStream = FileOutputStream(file)

            val writer = PdfWriter(outputStream)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)

            val title = Paragraph(titleText)
                .setBold()
                .setFontSize(18f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(title)

            val table = Table(UnitValue.createPercentArray(floatArrayOf(8f, 18f, 18f, 16f, 20f, 20f)))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setWidth(UnitValue.createPercentValue(100f))

            addTableHeader(table, listOf("#", "User", "Location", "Appliance", "Description", "Status"))

            complaints.forEachIndexed { index, c ->
                table.addCell(Cell().add(Paragraph("${index + 1}").setTextAlignment(TextAlignment.LEFT)))
                table.addCell(Cell().add(Paragraph(c.userType).setTextAlignment(TextAlignment.LEFT)))
                table.addCell(Cell().add(Paragraph("${c.building}/Fl ${c.floor}${c.room?.let { ", Rm $it" } ?: ""}").setTextAlignment(TextAlignment.LEFT)))
                table.addCell(Cell().add(Paragraph(c.appliance).setTextAlignment(TextAlignment.LEFT)))
                table.addCell(Cell().add(Paragraph(c.description).setTextAlignment(TextAlignment.LEFT)))
                val statusCell = Cell().add(Paragraph(c.status.name).setTextAlignment(TextAlignment.LEFT))
                when (c.status.name) {
                    "Resolved" -> statusCell.setBackgroundColor(ColorConstants.GREEN)
                    "Accepted" -> statusCell.setBackgroundColor(ColorConstants.BLUE)
                    else -> statusCell.setBackgroundColor(ColorConstants.RED)
                }
                table.addCell(statusCell)
            }

            document.add(table)
            document.close()

            onComplete(file)
            openPdf(file)
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(null)
        }
    }

    fun generateUsageLogsPdf(
        logs: List<String>,
        startDate: String,
        endDate: String,
        onComplete: (File?) -> Unit
    ) {
        try {
            val fileName = "Usage_Logs_${startDate}_to_${endDate}.pdf"
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)


            val outputStream = FileOutputStream(file)

            val writer = PdfWriter(outputStream)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)

            val title = Paragraph("Generator Usage Report")
                .setBold()
                .setFontSize(18f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(title)

            val dateRange = Paragraph("From $startDate to $endDate")
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
            document.add(dateRange)


            val table = Table(UnitValue.createPercentArray(floatArrayOf(5f, 30f, 30f, 35f)))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setWidth(UnitValue.createPercentValue(100f))

            addTableHeader(table, listOf("#", "Start Time", "End Time", "Duration"))


            logs.forEachIndexed { index, log ->
                val parts = log.split("|").map { part ->
                    part.replace("Start:", "")
                        .replace("End:", "")
                        .replace("Duration:", "")
                        .trim()
                }

                if (parts.size >= 3) {

                    table.addCell(Cell().add(Paragraph("${index + 1})").setTextAlignment(TextAlignment.LEFT)))

                    table.addCell(Cell().add(Paragraph(parts[0]).setTextAlignment(TextAlignment.LEFT)))


                    table.addCell(Cell().add(Paragraph(parts[1]).setTextAlignment(TextAlignment.LEFT)))


                    val durationCell = Cell().add(Paragraph(parts[2]).setTextAlignment(TextAlignment.LEFT))
                    durationCell.setBackgroundColor(ColorConstants.BLUE)
                    table.addCell(durationCell)
                }
            }

            document.add(table)
            document.close()

            onComplete(file)
            openPdf(file)

        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(null)
        }
    }

    private fun addTableHeader(table: Table, headers: List<String>) {
        headers.forEach { header ->
            table.addCell(
                Cell().add(Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }
    }


    private fun openPdf(file: File) {
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
        }
        context.startActivity(Intent.createChooser(intent, "Open PDF"))
    }

}