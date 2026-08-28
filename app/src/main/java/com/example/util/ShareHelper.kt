package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.BillEntity
import com.example.data.model.BillPageEntity
import com.example.data.model.BillRowEntity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ShareHelper {

  fun sharePdf(context: Context, pdfFile: File, billNumber: String) {
    try {
      val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        pdfFile
      )

      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Invoice $billNumber")
        putExtra(Intent.EXTRA_TEXT, "Here is your invoice #$billNumber from Bill Calculator.")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

      context.startActivity(Intent.createChooser(shareIntent, "Share Invoice PDF"))
    } catch (e: Exception) {
      Toast.makeText(context, "Failed to share PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
  }

  fun shareWhatsAppText(
    context: Context,
    bill: BillEntity,
    pages: List<Pair<BillPageEntity, List<BillRowEntity>>>,
    currencySymbol: String = "₹"
  ) {
    val builder = StringBuilder()
    builder.append("🧾 *BILL INVOICE: ${bill.invoiceNumber}*\n")
    if (bill.customerName.isNotBlank()) {
      builder.append("👤 *Customer:* ${bill.customerName}")
      if (bill.phone.isNotBlank()) builder.append(" | 📞 ${bill.phone}")
      builder.append("\n")
    }
    builder.append("📅 *Date:* ${Formatters.formatDate(bill.dateMillis)}\n")
    builder.append("━━━━━━━━━━━━━━━━━━━━\n")

    for ((page, rows) in pages) {
      val validRows = rows.filter { it.productName.isNotBlank() || (it.quantity != null && it.rate != null) }
      if (validRows.isNotEmpty()) {
        builder.append("📄 *Page ${page.pageNumber}*\n")
        for ((idx, row) in validRows.withIndex()) {
          val name = if (row.productName.isNotBlank()) row.productName else "Item ${idx + 1}"
          val qty = Formatters.formatQuantity(row.quantity)
          val rate = Formatters.formatMoneyValue(row.rate)
          val total = Formatters.formatCurrency(row.total, currencySymbol)
          builder.append("• $name ($qty × $rate) = $total\n")
        }
        builder.append("  *Page ${page.pageNumber} Subtotal:* ${Formatters.formatCurrency(page.pageTotal, currencySymbol)}\n\n")
      }
    }

    builder.append("━━━━━━━━━━━━━━━━━━━━\n")
    builder.append("💰 *GROSS TOTAL: ${Formatters.formatCurrency(bill.grossTotal, currencySymbol)}*\n")
    if (bill.note.isNotBlank()) {
      builder.append("📝 *Note:* ${bill.note}\n")
    }
    builder.append("\n_Thank you for your business!_")

    val textToShare = builder.toString()
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_TEXT, textToShare)
    }

    // Try sharing to WhatsApp specifically if preferred, or standard chooser
    try {
      sendIntent.setPackage("com.whatsapp")
      context.startActivity(sendIntent)
    } catch (e: Exception) {
      // Fallback to chooser
      val generalIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, textToShare)
      }
      context.startActivity(Intent.createChooser(generalIntent, "Share Bill Summary"))
    }
  }

  fun printPdf(context: Context, pdfFile: File, jobName: String) {
    try {
      val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
      if (printManager != null) {
        val printAdapter = object : PrintDocumentAdapter() {
          override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes?,
            cancellationSignal: android.os.CancellationSignal?,
            callback: LayoutResultCallback?,
            extras: android.os.Bundle?
          ) {
            if (cancellationSignal?.isCanceled == true) {
              callback?.onLayoutCancelled()
              return
            }
            val builder = android.print.PrintDocumentInfo.Builder("$jobName.pdf")
              .setContentType(android.print.PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
              .setPageCount(android.print.PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
              .build()
            callback?.onLayoutFinished(builder, true)
          }

          override fun onWrite(
            pages: Array<out android.print.PageRange>?,
            destination: android.os.ParcelFileDescriptor?,
            cancellationSignal: android.os.CancellationSignal?,
            callback: WriteResultCallback?
          ) {
            try {
              val input = FileInputStream(pdfFile)
              val output = FileOutputStream(destination?.fileDescriptor)
              val buf = ByteArray(1024)
              var bytesRead: Int
              while (input.read(buf).also { bytesRead = it } > 0) {
                output.write(buf, 0, bytesRead)
              }
              callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
              input.close()
              output.close()
            } catch (e: Exception) {
              callback?.onWriteFailed(e.message)
            }
          }
        }

        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
      } else {
        Toast.makeText(context, "Printing not supported on this device", Toast.LENGTH_SHORT).show()
      }
    } catch (e: Exception) {
      Toast.makeText(context, "Print error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
  }
}
