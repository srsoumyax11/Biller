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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun BillHistoryDetailScreen(
  billState: ActiveBillState,
  onBackClick: () -> Unit,
  onEditClick: () -> Unit,
  onDuplicateClick: () -> Unit,
  onDeleteClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val ledgerColors = LocalLedgerColors.current
  val context = LocalContext.current
  var showDeleteDialog by remember { mutableStateOf(false) }

  val billEntity = BillEntity(
    id = billState.billId,
    customerName = billState.customerName,
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
        orderIndex = idx
      )
    }
    Pair(pageEntity, rowEntities)
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("bill_history_detail_screen"),
    containerColor = ledgerColors.ledgerPaper,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = billState.invoiceNumber,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = Color.White
            )
            Text(
              text = "Archived Ledger Bill",
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.7f)
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }
        },
        actions = {
          IconButton(
            onClick = onEditClick,
            modifier = Modifier.testTag("history_edit_button")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Reopen & Edit",
              tint = Color.White
            )
          }
          IconButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.testTag("history_delete_button")
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = ledgerColors.inkNavy
        )
      )
    },
    bottomBar = {
      Surface(
        color = ledgerColors.inkNavy,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Reopen and Edit
            Button(
              onClick = onEditClick,
              modifier = Modifier
                .weight(1.2f)
                .height(46.dp)
                .testTag("history_reopen_edit_button"),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = ledgerColors.stampAmber,
                contentColor = Color.White
              )
            ) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Reopen & Edit", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Share PDF
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
                .testTag("history_share_pdf_button"),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = ledgerColors.inkNavy
              )
            ) {
              Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("PDF", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Share WhatsApp
            Button(
              onClick = {
                ShareHelper.shareWhatsAppText(context, billEntity, exportPages, billState.currencySymbol)
              },
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("history_whatsapp_button"),
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
      // Header Card
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
                  text = "CUSTOMER",
                  style = MaterialTheme.typography.labelSmall,
                  color = ledgerColors.mutedCharcoal
                )
                Text(
                  text = if (billState.customerName.isNotBlank()) billState.customerName else "Walk-in Customer",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = ledgerColors.inkNavy
                )
                if (billState.phone.isNotBlank()) {
                  Text(
                    text = "Phone: ${billState.phone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ledgerColors.mutedCharcoal
                  )
                }
              }

              SavedStampBadge(text = "SAVED BILL")
            }

            HorizontalDivider(
              modifier = Modifier.padding(vertical = 10.dp),
              color = ledgerColors.ruledLine
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Date: ${Formatters.formatDate(billState.dateMillis)}",
                style = MaterialTheme.typography.bodySmall,
                color = ledgerColors.mutedCharcoal
              )
              Text(
                text = "${billState.pages.size} pages • ${billState.totalItemCount} items",
                style = MaterialTheme.typography.bodySmall,
                color = ledgerColors.mutedCharcoal
              )
            }

            if (billState.note.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Note: ${billState.note}",
                style = MaterialTheme.typography.bodySmall,
                color = ledgerColors.mutedCharcoal
              )
            }
          }
        }
      }

      // Pages Breakdown
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
            // Page Title Strip
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(ledgerColors.inkNavyLight)
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
                text = Formatters.formatCurrency(page.pageTotal, billState.currencySymbol),
                style = LedgerNumeralMedium,
                color = Color(0xFF4ADE80),
                fontSize = 14.sp
              )
            }

            // Table Header
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(ledgerColors.ledgerPaperShaded)
                .padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("#", style = MaterialTheme.typography.labelSmall, color = ledgerColors.mutedCharcoal, modifier = Modifier.width(22.dp))
              Text("ITEM", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ledgerColors.inkNavy, modifier = Modifier.weight(1.5f))
              Text("QTY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ledgerColors.inkNavy, textAlign = TextAlign.End, modifier = Modifier.weight(0.7f))
              Text("RATE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ledgerColors.inkNavy, textAlign = TextAlign.End, modifier = Modifier.weight(0.8f))
              Text("TOTAL", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ledgerColors.inkNavy, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
            }

            HorizontalDivider(thickness = 1.dp, color = ledgerColors.ruledLine)

            filledRows.forEachIndexed { rowIdx, row ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("${rowIdx + 1}", style = LedgerNumeralRegular, fontSize = 11.sp, color = ledgerColors.mutedCharcoal, modifier = Modifier.width(22.dp))
                Text(
                  text = row.productName.ifBlank { "Item ${rowIdx + 1}" },
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Medium,
                  color = ledgerColors.charcoal,
                  modifier = Modifier.weight(1.5f)
                )
                Text(
                  text = Formatters.formatQuantity(row.quantity),
                  style = LedgerNumeralRegular,
                  fontSize = 12.sp,
                  textAlign = TextAlign.End,
                  color = ledgerColors.charcoal,
                  modifier = Modifier.weight(0.7f)
                )
                Text(
                  text = Formatters.formatMoneyValue(row.rate),
                  style = LedgerNumeralRegular,
                  fontSize = 12.sp,
                  textAlign = TextAlign.End,
                  color = ledgerColors.charcoal,
                  modifier = Modifier.weight(0.8f)
                )
                Text(
                  text = Formatters.formatMoneyValue(row.total),
                  style = LedgerNumeralRegular,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  textAlign = TextAlign.End,
                  color = ledgerColors.ledgerGreen,
                  modifier = Modifier.weight(1f)
                )
              }
              HorizontalDivider(thickness = 0.8.dp, color = ledgerColors.ruledLine)
            }
          }
        }
      }

      // Gross Total Card
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = ledgerColors.inkNavy),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "GROSS TOTAL",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 1.sp
              )
              Text(
                text = "Total across all ${billState.pages.size} pages",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
              )
            }

            Text(
              text = Formatters.formatCurrency(billState.grossTotal, billState.currencySymbol),
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

  // Delete Confirmation Dialog
  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text("Delete Bill?") },
      text = { Text("Are you sure you want to permanently delete this billing record? This action cannot be undone.") },
      confirmButton = {
        Button(
          onClick = {
            showDeleteDialog = false
            onDeleteClick()
          },
          colors = ButtonDefaults.buttonColors(containerColor = ledgerColors.softRed)
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
