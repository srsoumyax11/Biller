package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BillEntity
import com.example.data.model.BillPageEntity
import com.example.data.model.BillRowEntity
import com.example.ui.components.SavedStampBadge
import com.example.ui.theme.LedgerNumeralLarge
import com.example.ui.theme.LedgerNumeralMedium
import com.example.ui.theme.LedgerNumeralRegular
import com.example.ui.theme.LocalLedgerColors
import com.example.ui.viewmodel.ActiveBillState
import com.example.util.Formatters
import com.example.util.PdfExporter
import com.example.util.ShareHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillSummaryScreen(
  billState: ActiveBillState,
  onBackClick: () -> Unit,
  onEditBillClick: () -> Unit,
  onHomeClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val ledgerColors = LocalLedgerColors.current
  val context = LocalContext.current

  // Prepare bill entity & pages for export helpers
  val billEntity = BillEntity(
    id = billState.billId,
    customerName = billState.customerName,
    sellerName = billState.sellerName.ifBlank { billState.customerName },
    claimedTotal = billState.claimedTotal,
    deductionAmount = billState.deductionAmount,
    phone = billState.phone,
    dateMillis = billState.dateMillis,
    invoiceNumber = billState.invoiceNumber,
    note = billState.note,
    grossTotal = billState.grossTotal
  )

  val exportPages = billState.pages.map { page ->
    val pageEntity = BillPageEntity(
      billId = billState.billId,
      pageNumber = page.pageNumber,
      pageTotal = page.pageTotal,
      isSaved = page.isSaved
    )
    val rowEntities = page.rows.filter { it.isRowFilled }.mapIndexed { idx, row ->
      BillRowEntity(
        productName = row.productName,
        quantity = row.quantity,
        rate = row.rate,
        total = row.total,
        isReturn = row.isReturn,
        orderIndex = idx
      )
    }
    Pair(pageEntity, rowEntities)
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("bill_summary_screen"),
    containerColor = ledgerColors.ledgerPaper,
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Verification Report",
            fontWeight = FontWeight.Bold,
            color = ledgerColors.headerContent
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = ledgerColors.headerContent
            )
          }
        },
        actions = {
          IconButton(
            onClick = onEditBillClick,
            modifier = Modifier.testTag("summary_edit_button")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Bill",
              tint = ledgerColors.headerContent
            )
          }
          IconButton(
            onClick = onHomeClick,
            modifier = Modifier.testTag("summary_home_button")
          ) {
            Icon(
              imageVector = Icons.Default.Home,
              contentDescription = "Home",
              tint = ledgerColors.headerContent
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = ledgerColors.headerSurface
        )
      )
    },
    bottomBar = {
      Surface(
        color = ledgerColors.headerSurface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
        ) {
          // Export Actions Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // PDF Export
            Button(
              onClick = {
                try {
                  val pdf = PdfExporter.generateBillPdf(context, billEntity, exportPages, billState.currencySymbol)
                  ShareHelper.sharePdf(context, pdf, billState.invoiceNumber)
                } catch (e: Exception) {
                  Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
              },
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("export_pdf_button"),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = ledgerColors.stampAmber,
                contentColor = Color.White
              )
            ) {
              Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Export PDF", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // WhatsApp Share
            Button(
              onClick = {
                ShareHelper.shareWhatsAppText(context, billEntity, exportPages, billState.currencySymbol)
              },
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("share_whatsapp_button"),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25D366),
                contentColor = Color.White
              )
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Print
            OutlinedButton(
              onClick = {
                try {
                  val pdf = PdfExporter.generateBillPdf(context, billEntity, exportPages, billState.currencySymbol)
                  ShareHelper.printPdf(context, pdf, billState.invoiceNumber)
                } catch (e: Exception) {
                  Toast.makeText(context, "Print error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
              },
              modifier = Modifier
                .height(46.dp)
                .testTag("print_button"),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
              )
            ) {
              Icon(Icons.Default.Print, contentDescription = "Print", tint = Color.White, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Audit Verdict / Comparison Banner (Ultra-Clear High Contrast)
      if (billState.hasClaimedTotal) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = when {
                billState.isExactMatch -> Color(0xFF14532D)
                billState.isOvercharged -> Color(0xFF7F1D1D)
                else -> Color(0xFF1E293B)
              }
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("audit_verdict_card")
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = if (billState.isExactMatch) Icons.Default.CheckCircle else Icons.Default.WarningAmber,
                  contentDescription = null,
                  tint = if (billState.isExactMatch) Color(0xFF4ADE80) else Color(0xFFFCA5A5),
                  modifier = Modifier.size(24.dp)
                )
                Text(
                  text = when {
                    billState.isExactMatch -> "AUDIT VERDICT: EXACT MATCH"
                    billState.isOvercharged -> "AUDIT VERDICT: OVERCHARGED"
                    else -> "AUDIT VERDICT: DISCREPANCY DETECTED"
                  },
                  fontWeight = FontWeight.Black,
                  fontSize = 14.sp,
                  letterSpacing = 0.5.sp,
                  color = if (billState.isExactMatch) Color(0xFF4ADE80) else Color(0xFFFCA5A5)
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text("SELLER'S PAPER BILL", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  Text(
                    text = Formatters.formatCurrency(billState.claimedTotal, billState.currencySymbol),
                    style = LedgerNumeralLarge,
                    fontSize = 19.sp,
                    color = Color.White
                  )
                }

                Column(
                  modifier = Modifier.weight(1f),
                  horizontalAlignment = Alignment.End
                ) {
                  Text("CALCULATED NET", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  Text(
                    text = Formatters.formatCurrency(billState.netTotal, billState.currencySymbol),
                    style = LedgerNumeralLarge,
                    fontSize = 19.sp,
                    color = Color(0xFF4ADE80)
                  )
                }
              }

              if (!billState.isExactMatch) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color.White.copy(alpha = 0.25f))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = if (billState.isOvercharged) "You are being overcharged by:" else "Calculation difference:",
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium
                  )
                  Text(
                    text = Formatters.formatCurrency(billState.discrepancy, billState.currencySymbol),
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = if (billState.isOvercharged) Color(0xFFFCA5A5) else Color(0xFFFDE047)
                  )
                }
              }
            }
          }
        }
      }

      // Bill Header Parchi Card
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = ledgerColors.ledgerPaperSurface),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ledgerColors.ruledLine, RoundedCornerShape(12.dp))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "PARCHI / BILL #",
                  style = MaterialTheme.typography.labelMedium,
                  color = ledgerColors.mutedCharcoal,
                  letterSpacing = 1.sp
                )
                Text(
                  text = billState.invoiceNumber,
                  style = MaterialTheme.typography.titleLarge,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = ledgerColors.inkNavy
                )
              }

              SavedStampBadge(
                text = if (billState.hasClaimedTotal && billState.isExactMatch) "AUDIT OK" else "VERIFIED",
                rotationDegrees = 4f
              )
            }

            HorizontalDivider(
              modifier = Modifier.padding(vertical = 12.dp),
              color = ledgerColors.ruledLine
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("SELLER / SHOP", style = MaterialTheme.typography.labelSmall, color = ledgerColors.mutedCharcoal)
                Text(
                  text = billState.displayName,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  color = ledgerColors.charcoal
                )
                if (billState.phone.isNotBlank()) {
                  Text(
                    text = "Phone: ${billState.phone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ledgerColors.mutedCharcoal
                  )
                }
              }

              Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
              ) {
                Text("DATE", style = MaterialTheme.typography.labelSmall, color = ledgerColors.mutedCharcoal)
                Text(
                  text = Formatters.formatDate(billState.dateMillis),
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  color = ledgerColors.charcoal
                )
                Text(
                  text = "${billState.pages.size} pages verified",
                  style = MaterialTheme.typography.bodySmall,
                  color = ledgerColors.mutedCharcoal
                )
              }
            }

            if (billState.note.isNotBlank()) {
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "Note: ${billState.note}",
                style = MaterialTheme.typography.bodySmall,
                color = ledgerColors.mutedCharcoal
              )
            }
          }
        }
      }

      // Page Breakdown Tables
      items(
        count = billState.pages.size,
        key = { billState.pages[it].id }
      ) { pageIdx ->
        val page = billState.pages[pageIdx]
        val filledRows = page.rows.filter { it.isRowFilled }

        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = ledgerColors.ledgerPaperSurface),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ledgerColors.ruledLine, RoundedCornerShape(10.dp))
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            // Page Header
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(ledgerColors.tableHeaderBg)
                .padding(horizontal = 14.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "PAGE ${page.pageNumber}",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 13.sp
              )
              Text(
                text = "${filledRows.size} items",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp
              )
            }

            // Table Columns Header (Crisp Alignment)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(ledgerColors.ledgerPaperShaded)
                .padding(horizontal = 12.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("#", style = MaterialTheme.typography.labelSmall, color = ledgerColors.inkNavy, fontWeight = FontWeight.Bold, modifier = Modifier.width(26.dp))
              Text("ITEM DESCRIPTION", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ledgerColors.inkNavy, modifier = Modifier.weight(2.4f))
              Text("QTY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ledgerColors.inkNavy, textAlign = TextAlign.End, modifier = Modifier.weight(0.9f))
              Text("RATE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ledgerColors.inkNavy, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
              Text("TOTAL (${billState.currencySymbol})", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ledgerColors.inkNavy, textAlign = TextAlign.End, modifier = Modifier.weight(1.3f))
            }

            HorizontalDivider(thickness = 1.dp, color = ledgerColors.ruledLine)

            if (filledRows.isEmpty()) {
              Text(
                text = "No items on this page",
                style = MaterialTheme.typography.bodySmall,
                color = ledgerColors.mutedCharcoal,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
              )
            } else {
              filledRows.forEachIndexed { rowIdx, row ->
                val isReturnRow = row.isReturn
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isReturnRow) ledgerColors.softRedBg else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "${rowIdx + 1}",
                    style = LedgerNumeralRegular,
                    fontSize = 11.5.sp,
                    color = if (isReturnRow) ledgerColors.softRed else ledgerColors.mutedCharcoal,
                    modifier = Modifier.width(26.dp)
                  )

                  // Item Name with return tag
                  Row(
                    modifier = Modifier.weight(2.4f),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    if (isReturnRow) {
                      Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = ledgerColors.softRed,
                        modifier = Modifier.padding(end = 4.dp)
                      ) {
                        Text(
                          text = "RET",
                          color = Color.White,
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Black,
                          modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                        )
                      }
                    }
                    Text(
                      text = row.productName.ifBlank { "Item ${rowIdx + 1}" },
                      style = MaterialTheme.typography.bodySmall,
                      fontWeight = FontWeight.Medium,
                      color = if (isReturnRow) ledgerColors.softRed else ledgerColors.charcoal,
                      maxLines = 1
                    )
                  }

                  Text(
                    text = Formatters.formatQuantity(row.quantity),
                    style = LedgerNumeralRegular,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    color = if (isReturnRow) ledgerColors.softRed else ledgerColors.charcoal,
                    modifier = Modifier.weight(0.9f)
                  )

                  Text(
                    text = Formatters.formatMoneyValue(row.rate),
                    style = LedgerNumeralRegular,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    color = if (isReturnRow) ledgerColors.softRed else ledgerColors.charcoal,
                    modifier = Modifier.weight(1.0f)
                  )

                  val rowTotalStr = if (isReturnRow) {
                    "-${Formatters.formatMoneyValue(kotlin.math.abs(row.total))}"
                  } else {
                    Formatters.formatMoneyValue(row.total)
                  }

                  Text(
                    text = rowTotalStr,
                    style = LedgerNumeralRegular,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    color = if (isReturnRow) ledgerColors.softRed else ledgerColors.ledgerGreen,
                    modifier = Modifier.weight(1.3f)
                  )
                }
                HorizontalDivider(thickness = 0.8.dp, color = ledgerColors.ruledLine)
              }
            }

            // Page Subtotal Row
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(ledgerColors.ledgerPaperShaded)
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Page ${page.pageNumber} Subtotal",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = ledgerColors.inkNavy
              )
              Text(
                text = Formatters.formatCurrency(page.pageTotal, billState.currencySymbol),
                style = LedgerNumeralMedium,
                color = if (page.pageTotal < 0) ledgerColors.softRed else ledgerColors.ledgerGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
          }
        }
      }

      // Grand Summary & Net Payable Card (Hero Dark Container with 100% Contrast)
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = ledgerColors.heroCardContainer),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp)
          ) {
            // Breakdown lines
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Gross Items Total (${billState.totalItemCount} items)",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
              )
              Text(
                text = Formatters.formatCurrency(billState.grossTotal, billState.currencySymbol),
                style = LedgerNumeralRegular,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 14.sp
              )
            }

            if (billState.deductionAmount > 0.0) {
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Advance Paid / Discount / Deduction",
                  color = Color(0xFFFCA5A5),
                  fontSize = 13.sp
                )
                Text(
                  text = "-${Formatters.formatCurrency(billState.deductionAmount, billState.currencySymbol)}",
                  style = LedgerNumeralRegular,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFFFCA5A5),
                  fontSize = 14.sp
                )
              }
            }

            HorizontalDivider(
              modifier = Modifier.padding(vertical = 12.dp),
              color = Color.White.copy(alpha = 0.2f)
            )

            // Net Final Payable Highlight
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "NET CALCULATED PAYABLE",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Black,
                  color = Color.White,
                  letterSpacing = 1.sp
                )
                Text(
                  text = "Verified across ${billState.pages.size} pages",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.6f)
                )
              }

              Text(
                text = Formatters.formatCurrency(billState.netTotal, billState.currencySymbol),
                style = LedgerNumeralLarge,
                color = Color(0xFF4ADE80),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }
    }
  }
}
