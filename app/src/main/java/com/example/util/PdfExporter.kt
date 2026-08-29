package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.model.BillEntity
import com.example.data.model.BillPageEntity
import com.example.data.model.BillRowEntity
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

  fun generateBillPdf(
    context: Context,
    bill: BillEntity,
    pages: List<Pair<BillPageEntity, List<BillRowEntity>>>,
    currencySymbol: String = "₹"
  ): File {
    val pdfDocument = PdfDocument()
    val pageWidth = 595 // A4 standard width in points
    val pageHeight = 842 // A4 standard height in points

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var currentPageNumber = 1
    val totalPagesCount = pages.size.coerceAtLeast(1)

    for ((pageEntity, rowsList) in pages) {
      val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
      val page = pdfDocument.startPage(pageInfo)
      val canvas: Canvas = page.canvas

      // Draw Paper Background
      paint.color = Color.parseColor("#FAF8F3")
      paint.style = Paint.Style.FILL
      canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

      // Top Header Navy Bar
      paint.color = Color.parseColor("#1E2A44")
      canvas.drawRect(0f, 0f, pageWidth.toFloat(), 64f, paint)

      // Header Text
      textPaint.color = Color.WHITE
      textPaint.textSize = 18f
      textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      canvas.drawText("BILL VERIFIER • AUDIT REPORT", 32f, 40f, textPaint)

      textPaint.textSize = 11f
      textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
      val pageIndicatorText = "Page ${pageEntity.pageNumber} of $totalPagesCount"
      val pageIndWidth = textPaint.measureText(pageIndicatorText)
      canvas.drawText(pageIndicatorText, pageWidth - 32f - pageIndWidth, 40f, textPaint)

      // Bill Details Box
      var currentY = 88f
      paint.color = Color.parseColor("#FFFFFF")
      paint.style = Paint.Style.FILL
      canvas.drawRoundRect(RectF(32f, currentY, pageWidth - 32f, currentY + 70f), 8f, 8f, paint)

      paint.color = Color.parseColor("#D8D2C4")
      paint.style = Paint.Style.STROKE
      paint.strokeWidth = 1f
      canvas.drawRoundRect(RectF(32f, currentY, pageWidth - 32f, currentY + 70f), 8f, 8f, paint)

      // Seller / Shop Info text
      textPaint.color = Color.parseColor("#1E2A44")
      textPaint.textSize = 13f
      textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      val sellerName = if (bill.sellerName.isNotBlank()) bill.sellerName else if (bill.customerName.isNotBlank()) bill.customerName else "Unknown Seller"
      canvas.drawText("Seller / Shop: $sellerName", 46f, currentY + 24f, textPaint)

      textPaint.color = Color.parseColor("#6B7280")
      textPaint.textSize = 11f
      textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
      val phoneText = if (bill.phone.isNotBlank()) "Phone: ${bill.phone}" else "Phone: N/A"
      canvas.drawText(phoneText, 46f, currentY + 44f, textPaint)

      val dateText = "Date: ${Formatters.formatDate(bill.dateMillis)}"
      val invText = "Bill / Parchi #: ${bill.invoiceNumber}"
      val dateWidth = textPaint.measureText(dateText)
      val invWidth = textPaint.measureText(invText)

      textPaint.color = Color.parseColor("#1E2A44")
      textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      canvas.drawText(invText, pageWidth - 46f - invWidth, currentY + 24f, textPaint)

      textPaint.color = Color.parseColor("#6B7280")
      textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
      canvas.drawText(dateText, pageWidth - 46f - dateWidth, currentY + 44f, textPaint)

      // Table Header
      currentY = 176f
      paint.color = Color.parseColor("#1E2A44")
      paint.style = Paint.Style.FILL
      canvas.drawRoundRect(RectF(32f, currentY, pageWidth - 32f, currentY + 28f), 4f, 4f, paint)

      textPaint.color = Color.WHITE
      textPaint.textSize = 11f
      textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)

      canvas.drawText("#", 44f, currentY + 18f, textPaint)
      canvas.drawText("ITEM DESCRIPTION", 76f, currentY + 18f, textPaint)
      canvas.drawText("QTY", 330f, currentY + 18f, textPaint)
      canvas.drawText("RATE", 410f, currentY + 18f, textPaint)
      canvas.drawText("TOTAL ($currencySymbol)", 480f, currentY + 18f, textPaint)

      currentY += 28f

      // Rows
      val filteredRows = rowsList.filter { it.productName.isNotBlank() || (it.quantity != null && it.rate != null) }
      val rowHeight = 26f

      paint.style = Paint.Style.STROKE
      paint.strokeWidth = 0.8f
      paint.color = Color.parseColor("#D8D2C4")

      for ((index, row) in filteredRows.withIndex()) {
        val rowY = currentY + (index * rowHeight)
        val isReturn = row.isReturn

        // Alternate row fill or return row fill
        val fillPaint = Paint()
        fillPaint.style = Paint.Style.FILL
        if (isReturn) {
          fillPaint.color = Color.parseColor("#FEE2E2")
          canvas.drawRect(32f, rowY, pageWidth - 32f, rowY + rowHeight, fillPaint)
        } else if (index % 2 == 1) {
          fillPaint.color = Color.parseColor("#F5F2EB")
          canvas.drawRect(32f, rowY, pageWidth - 32f, rowY + rowHeight, fillPaint)
        }

        // Ruled line
        canvas.drawLine(32f, rowY + rowHeight, pageWidth - 32f, rowY + rowHeight, paint)

        textPaint.color = if (isReturn) Color.parseColor("#991B1B") else Color.parseColor("#2B2B2B")
        textPaint.textSize = 11f
        textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("${index + 1}", 44f, rowY + 18f, textPaint)

        val prodName = if (row.productName.isNotBlank()) row.productName else "Item ${index + 1}"
        val returnPrefix = if (isReturn) "[RET] " else ""
        val fullItemName = returnPrefix + prodName
        val truncatedName = if (fullItemName.length > 30) fullItemName.take(28) + "…" else fullItemName
        canvas.drawText(truncatedName, 76f, rowY + 18f, textPaint)

        // Monospace Numbers
        textPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        val qtyStr = Formatters.formatQuantity(row.quantity)
        canvas.drawText(qtyStr, 330f, rowY + 18f, textPaint)

        val rateStr = Formatters.formatMoneyValue(row.rate)
        canvas.drawText(rateStr, 410f, rowY + 18f, textPaint)

        val totalStr = if (isReturn) {
          "-${Formatters.formatMoneyValue(kotlin.math.abs(row.total))}"
        } else if (row.total > 0) {
          Formatters.formatMoneyValue(row.total)
        } else {
          "-"
        }
        textPaint.color = if (isReturn) Color.parseColor("#B91C1C") else Color.parseColor("#15803D")
        textPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        canvas.drawText(totalStr, 480f, rowY + 18f, textPaint)
      }

      val tableBottomY = currentY + (filteredRows.size * rowHeight)

      // Page Total Section
      val pageTotalY = tableBottomY + 16f
      paint.color = Color.parseColor("#E8F5EE")
      paint.style = Paint.Style.FILL
      canvas.drawRoundRect(RectF(280f, pageTotalY, pageWidth - 32f, pageTotalY + 36f), 6f, 6f, paint)

      paint.color = Color.parseColor("#2F6F4E")
      paint.style = Paint.Style.STROKE
      paint.strokeWidth = 1f
      canvas.drawRoundRect(RectF(280f, pageTotalY, pageWidth - 32f, pageTotalY + 36f), 6f, 6f, paint)

      textPaint.color = Color.parseColor("#2F6F4E")
      textPaint.textSize = 12f
      textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      canvas.drawText("PAGE ${pageEntity.pageNumber} TOTAL:", 296f, pageTotalY + 23f, textPaint)

      val pageTotalStr = Formatters.formatCurrency(pageEntity.pageTotal, currencySymbol)
      val pageTotWidth = textPaint.measureText(pageTotalStr)
      textPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
      canvas.drawText(pageTotalStr, pageWidth - 44f - pageTotWidth, pageTotalY + 23f, textPaint)

      // Page Verified Stamp
      paint.color = Color.parseColor("#2F6F4E")
      paint.style = Paint.Style.FILL
      canvas.drawRoundRect(RectF(32f, pageTotalY + 4f, 150f, pageTotalY + 32f), 4f, 4f, paint)
      textPaint.color = Color.WHITE
      textPaint.textSize = 10f
      textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      canvas.drawText("✓ PAGE VERIFIED", 44f, pageTotalY + 22f, textPaint)

      // Bottom Section on the Last Page (Gross Total, Audit Verdict, & Notes)
      if (currentPageNumber == totalPagesCount) {
        var grossY = pageTotalY + 52f

        // Audit verdict banner if claimedTotal > 0
        if (bill.claimedTotal > 0.0) {
          val auditBoxHeight = 48f
          paint.color = if (bill.isExactMatch) Color.parseColor("#14532D") else Color.parseColor("#7F1D1D")
          paint.style = Paint.Style.FILL
          canvas.drawRoundRect(RectF(32f, grossY, pageWidth - 32f, grossY + auditBoxHeight), 6f, 6f, paint)

          textPaint.color = Color.WHITE
          textPaint.textSize = 11f
          textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
          val auditTitle = if (bill.isExactMatch) "AUDIT RESULT: EXACT MATCH (No Error)" else "AUDIT RESULT: DISCREPANCY FOUND"
          canvas.drawText(auditTitle, 46f, grossY + 20f, textPaint)

          textPaint.textSize = 10f
          textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
          val comparisonText = "Seller's Paper Total: ${Formatters.formatCurrency(bill.claimedTotal, currencySymbol)} | Calculated Net: ${Formatters.formatCurrency(bill.netTotal, currencySymbol)}"
          canvas.drawText(comparisonText, 46f, grossY + 36f, textPaint)

          if (!bill.isExactMatch) {
            val diffText = if (bill.isOvercharged) "Overcharge: +${Formatters.formatCurrency(bill.discrepancy, currencySymbol)}" else "Discrepancy: ${Formatters.formatCurrency(bill.discrepancy, currencySymbol)}"
            val diffWidth = textPaint.measureText(diffText)
            textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textPaint.color = Color.parseColor("#FCA5A5")
            canvas.drawText(diffText, pageWidth - 46f - diffWidth, grossY + 28f, textPaint)
          }

          grossY += auditBoxHeight + 12f
        }

        // Notes if any
        if (bill.note.isNotBlank()) {
          textPaint.color = Color.parseColor("#6B7280")
          textPaint.textSize = 10f
          textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
          canvas.drawText("Note: ${bill.note}", 32f, grossY + 20f, textPaint)
        }

        // Grand Calculated Total Box
        paint.color = Color.parseColor("#1E2A44")
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(RectF(220f, grossY, pageWidth - 32f, grossY + 60f), 8f, 8f, paint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 11f
        textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("NET PAYABLE:", 236f, grossY + 24f, textPaint)

        if (bill.deductionAmount > 0.0) {
          textPaint.textSize = 9.5f
          textPaint.color = Color.parseColor("#FCA5A5")
          canvas.drawText("(Deduction: -${Formatters.formatCurrency(bill.deductionAmount, currencySymbol)})", 236f, grossY + 42f, textPaint)
        }

        val netTotalStr = Formatters.formatCurrency(bill.netTotal, currencySymbol)
        textPaint.color = Color.parseColor("#4ADE80")
        textPaint.textSize = 17f
        textPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        val netWidth = textPaint.measureText(netTotalStr)
        canvas.drawText(netTotalStr, pageWidth - 44f - netWidth, grossY + 38f, textPaint)
      }

      // Footer
      textPaint.color = Color.parseColor("#9CA3AF")
      textPaint.textSize = 9f
      textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
      val footer = "Verified via Bill Verifier • Paper Bill & Ledger Calculation Audit"
      val footerWidth = textPaint.measureText(footer)
      canvas.drawText(footer, (pageWidth - footerWidth) / 2f, pageHeight - 24f, textPaint)

      pdfDocument.finishPage(page)
      currentPageNumber++
    }

    // Save to Cache Directory
    val cacheDir = File(context.cacheDir, "bills")
    if (!cacheDir.exists()) {
      cacheDir.mkdirs()
    }
    val safeInvoice = if (bill.invoiceNumber.isNotBlank()) bill.invoiceNumber.replace("/", "_") else "bill_${bill.id}"
    val pdfFile = File(cacheDir, "${safeInvoice}.pdf")
    val fileOutputStream = FileOutputStream(pdfFile)
    pdfDocument.writeTo(fileOutputStream)
    fileOutputStream.flush()
    fileOutputStream.close()
    pdfDocument.close()

    return pdfFile
  }
}
