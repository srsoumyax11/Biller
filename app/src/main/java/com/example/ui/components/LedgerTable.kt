package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LedgerNumeralMedium
import com.example.ui.theme.LedgerNumeralRegular
import com.example.ui.theme.LocalLedgerColors
import com.example.ui.viewmodel.UiPage
import com.example.ui.viewmodel.UiRow
import com.example.util.Formatters

@Composable
fun LedgerTable(
  page: UiPage,
  pageIndex: Int,
  currencySymbol: String,
  isAnimatedStamp: Boolean,
  onRowChange: (rowIndex: Int, name: String?, qty: String?, rate: String?) -> Unit,
  onDeleteRow: (rowIndex: Int) -> Unit,
  onToggleLock: () -> Unit,
  modifier: Modifier = Modifier
) {
  val ledgerColors = LocalLedgerColors.current
  val focusManager = LocalFocusManager.current

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("ledger_table_${page.pageNumber}"),
    shape = RoundedCornerShape(8.dp),
    color = ledgerColors.ledgerPaperSurface,
    tonalElevation = 1.dp,
    shadowElevation = 1.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, ledgerColors.ruledLine, RoundedCornerShape(8.dp))
    ) {

      // Ledger Table Header (High Density: #F1EFE9 with uppercase tracking-widest text-[#1E2A44]/60)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(ledgerColors.ledgerPaperShaded)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "PRODUCT",
          style = MaterialTheme.typography.labelSmall,
          color = ledgerColors.inkNavy.copy(alpha = 0.6f),
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp,
          letterSpacing = 1.2.sp,
          modifier = Modifier.weight(5f)
        )

        Text(
          text = "QTY",
          style = MaterialTheme.typography.labelSmall,
          color = ledgerColors.inkNavy.copy(alpha = 0.6f),
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp,
          letterSpacing = 1.2.sp,
          textAlign = TextAlign.End,
          modifier = Modifier.weight(2f)
        )

        Text(
          text = "RATE",
          style = MaterialTheme.typography.labelSmall,
          color = ledgerColors.inkNavy.copy(alpha = 0.6f),
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp,
          letterSpacing = 1.2.sp,
          textAlign = TextAlign.End,
          modifier = Modifier.weight(2f)
        )

        Text(
          text = "TOTAL",
          style = MaterialTheme.typography.labelSmall,
          color = ledgerColors.inkNavy.copy(alpha = 0.6f),
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp,
          letterSpacing = 1.2.sp,
          textAlign = TextAlign.End,
          modifier = Modifier.weight(3f)
        )

        Spacer(modifier = Modifier.width(28.dp)) // space for delete action
      }

      HorizontalDivider(thickness = 1.dp, color = ledgerColors.ruledLine)

      // Rows
      page.rows.forEachIndexed { rowIndex, row ->
        LedgerRowItem(
          row = row,
          rowNumber = rowIndex + 1,
          isLocked = page.isSaved,
          isLastRow = rowIndex == page.rows.size - 1,
          onNameChange = { onRowChange(rowIndex, it, null, null) },
          onQtyChange = { onRowChange(rowIndex, null, it, null) },
          onRateChange = { onRowChange(rowIndex, null, null, it) },
          onDelete = { onDeleteRow(rowIndex) },
          onNextFocus = { focusManager.moveFocus(FocusDirection.Next) }
        )

        HorizontalDivider(thickness = 0.8.dp, color = ledgerColors.ruledLine)
      }

      // Page Total Subtotal Bar (High Density Layout)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(ledgerColors.ledgerPaperShaded)
          .border(
            width = 1.dp,
            color = ledgerColors.ruledLine
          )
          .padding(horizontal = 14.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = "PAGE ${page.pageNumber} TOTAL",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp,
              color = ledgerColors.inkNavy.copy(alpha = 0.6f)
            )
            Text(
              text = Formatters.formatMoneyValue(page.pageTotal),
              fontFamily = FontFamily.Monospace,
              color = ledgerColors.ledgerGreen,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.testTag("page_${page.pageNumber}_total")
            )
          }

          if (page.isSaved) {
            OutlinedButton(
              onClick = onToggleLock,
              modifier = Modifier
                .height(34.dp)
                .testTag("reopen_page_button"),
              shape = RoundedCornerShape(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Reopen Page",
                modifier = Modifier.size(13.dp),
                tint = ledgerColors.stampAmber
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Reopen",
                fontSize = 11.sp,
                color = ledgerColors.stampAmber,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Stamp badge overlay when saved
        if (page.isSaved) {
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .padding(start = 24.dp)
          ) {
            SavedStampBadge(
              text = "PAGE SAVED",
              isAnimated = isAnimatedStamp,
              rotationDegrees = -15f
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LedgerRowItem(
  row: UiRow,
  rowNumber: Int,
  isLocked: Boolean,
  isLastRow: Boolean,
  onNameChange: (String) -> Unit,
  onQtyChange: (String) -> Unit,
  onRateChange: (String) -> Unit,
  onDelete: () -> Unit,
  onNextFocus: () -> Unit
) {
  val ledgerColors = LocalLedgerColors.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(if (row.isRowFilled) Color.White.copy(alpha = 0.4f) else Color.Transparent)
      .padding(horizontal = 12.dp, vertical = 7.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Product Name Field
    Box(
      modifier = Modifier
        .weight(5f)
        .padding(end = 4.dp)
    ) {
      if (row.productName.isEmpty() && !isLocked) {
        Text(
          text = if (isLastRow) "Add item..." else "Item name",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = ledgerColors.charcoal.copy(alpha = 0.4f),
            fontSize = 13.5.sp,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
          )
        )
      }
      BasicTextField(
        value = row.productName,
        onValueChange = onNameChange,
        readOnly = isLocked,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
          color = ledgerColors.charcoal,
          fontSize = 13.5.sp,
          fontWeight = FontWeight.Normal
        ),
        cursorBrush = SolidColor(ledgerColors.stampAmber),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { onNextFocus() }),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("row_name_$rowNumber")
      )
    }

    // Quantity Field (Monospace, Right-Aligned, Decimal Keyboard)
    Box(
      modifier = Modifier
        .weight(2f)
        .padding(horizontal = 2.dp),
      contentAlignment = Alignment.CenterEnd
    ) {
      if (row.quantityStr.isEmpty() && !isLocked) {
        Text(
          text = "-",
          style = LedgerNumeralRegular.copy(
            color = ledgerColors.mutedCharcoal.copy(alpha = 0.4f),
            fontSize = 13.5.sp,
            textAlign = TextAlign.End
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }
      BasicTextField(
        value = row.quantityStr,
        onValueChange = onQtyChange,
        readOnly = isLocked,
        textStyle = LedgerNumeralRegular.copy(
          color = ledgerColors.charcoal,
          fontSize = 13.5.sp,
          textAlign = TextAlign.End
        ),
        cursorBrush = SolidColor(ledgerColors.stampAmber),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Decimal,
          imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { onNextFocus() }),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("row_qty_$rowNumber")
      )
    }

    // Rate Field (Monospace, Right-Aligned, Decimal Keyboard)
    Box(
      modifier = Modifier
        .weight(2f)
        .padding(horizontal = 2.dp),
      contentAlignment = Alignment.CenterEnd
    ) {
      if (row.rateStr.isEmpty() && !isLocked) {
        Text(
          text = "-",
          style = LedgerNumeralRegular.copy(
            color = ledgerColors.mutedCharcoal.copy(alpha = 0.4f),
            fontSize = 13.5.sp,
            textAlign = TextAlign.End
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }
      BasicTextField(
        value = row.rateStr,
        onValueChange = onRateChange,
        readOnly = isLocked,
        textStyle = LedgerNumeralRegular.copy(
          color = ledgerColors.charcoal,
          fontSize = 13.5.sp,
          textAlign = TextAlign.End
        ),
        cursorBrush = SolidColor(ledgerColors.stampAmber),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Decimal,
          imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { onNextFocus() }),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("row_rate_$rowNumber")
      )
    }

    // Total Amount (Calculated Live, Monospace Font-Semibold)
    val totalDisplay = if (row.hasCalculation && row.total > 0) {
      Formatters.formatMoneyValue(row.total)
    } else if (row.hasCalculation && row.total == 0.0) {
      "0.00"
    } else {
      "-"
    }

    Text(
      text = totalDisplay,
      style = LedgerNumeralRegular.copy(
        fontWeight = if (row.total > 0) FontWeight.SemiBold else FontWeight.Normal,
        fontSize = 13.5.sp,
        color = if (row.total > 0) ledgerColors.charcoal else ledgerColors.mutedCharcoal.copy(alpha = 0.4f),
        textAlign = TextAlign.End
      ),
      modifier = Modifier
        .weight(3f)
        .padding(start = 2.dp, end = 2.dp)
        .testTag("row_total_$rowNumber")
    )

    // Delete Button
    if (!isLocked && (row.isRowFilled || !isLastRow)) {
      IconButton(
        onClick = onDelete,
        modifier = Modifier
          .size(28.dp)
          .testTag("delete_row_$rowNumber")
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Delete Row",
          tint = ledgerColors.softRed.copy(alpha = 0.6f),
          modifier = Modifier.size(15.dp)
        )
      }
    } else {
      Spacer(modifier = Modifier.width(28.dp))
    }
  }
}
