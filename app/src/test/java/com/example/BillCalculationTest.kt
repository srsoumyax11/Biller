package com.example

import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.UiPage
import com.example.ui.viewmodel.UiRow
import com.example.util.Formatters
import org.junit.Assert.assertEquals
import org.junit.Test

class BillCalculationTest {

  @Test
  fun `row total calculation with decimals`() {
    val row = UiRow(
      productName = "Rice",
      quantityStr = "2.5",
      rateStr = "40.50"
    )
    assertEquals(101.25, row.total, 0.001)
  }

  @Test
  fun `page total sums all rows accurately`() {
    val rows = listOf(
      UiRow(productName = "Rice", quantityStr = "2", rateStr = "50"),
      UiRow(productName = "Sugar", quantityStr = "1.5", rateStr = "40"),
      UiRow(productName = "", quantityStr = "", rateStr = "") // empty trailing row
    )
    val page = UiPage(pageNumber = 1, rows = rows)
    assertEquals(160.0, page.pageTotal, 0.001)
  }

  @Test
  fun `formatter creates standard currency representation`() {
    val formatted = Formatters.formatCurrency(1250.75, "₹")
    assertEquals("₹ 1,250.75", formatted)
  }

  @Test
  fun `fast calculation mode without product name calculates total accurately`() {
    val row = UiRow(
      productName = "",
      quantityStr = "12",
      rateStr = "15.50"
    )
    assertEquals(186.0, row.total, 0.001)
    assertEquals(true, row.hasCalculation)
    assertEquals(true, row.isRowFilled)
  }
}
