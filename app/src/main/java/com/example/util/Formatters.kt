package com.example.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object Formatters {

  private val moneyFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))
  private val qtyFormat = DecimalFormat("#,##0.###", DecimalFormatSymbols(Locale.US))
  private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
  private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
  private val invDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

  fun formatCurrency(amount: Double?, currencySymbol: String = "₹"): String {
    if (amount == null) return ""
    return "$currencySymbol ${moneyFormat.format(amount)}"
  }

  fun formatMoneyValue(amount: Double?): String {
    if (amount == null) return ""
    return moneyFormat.format(amount)
  }

  fun formatQuantity(qty: Double?): String {
    if (qty == null) return ""
    return qtyFormat.format(qty)
  }

  fun formatDate(dateMillis: Long): String {
    return dateFormat.format(Date(dateMillis))
  }

  fun formatDateTime(dateMillis: Long): String {
    return dateTimeFormat.format(Date(dateMillis))
  }

  fun generateInvoiceNumber(): String {
    val dateStr = invDateFormat.format(Date())
    val randomSuffix = Random.nextInt(100, 999)
    return "INV-$dateStr-$randomSuffix"
  }

  fun parseDecimal(input: String): Double? {
    if (input.isBlank()) return null
    return try {
      input.trim().replace(",", "").toDoubleOrNull()
    } catch (e: Exception) {
      null
    }
  }
}
