package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
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
  showProductName: Boolean = true,
  onRowChange: (rowIndex: Int, name: String?, qty: String?, rate: String?) -> Unit,
  onDeleteRow: (rowIndex: Int) -> Unit,
  onAddNegativeRow: (() -> Unit)? = null,
  onAddPositiveRow: (() -> Unit)? = null,
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

      // Ledger Table Header (High Contrast High Density: bold labels)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(ledgerColors.tableHeaderBg)
          .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (showProductName) {
          Text(
            text = "PRODUCT / ITEM",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.weight(4.2f)
          )

          Text(
            text = "QTY",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.6f)
          )

          Text(
            text = "RATE",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.8f)
          )

          Text(
            text = "TOTAL ($currencySymbol)",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2.6f)
          )
        } else {
          // Fast Qty x Rate Mode without product name column
          Text(
            text = "ITEM #",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.weight(1.4f)
          )

          Text(
            text = "QUANTITY",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2.8f)
          )

          Text(
            text = "RATE ($currencySymbol)",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2.8f)
          )

          Text(
            text = "TOTAL ($currencySymbol)",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(3.2f)
          )
        }

        Spacer(modifier = Modifier.width(32.dp)) // space for delete icon column
      }

      HorizontalDivider(thickness = 1.dp, color = ledgerColors.ruledLine)

      // Rows
      page.rows.forEachIndexed { rowIndex, row ->
        LedgerRowItem(
          row = row,
          rowNumber = rowIndex + 1,
          showProductName = showProductName,
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

      // Dedicated Action Buttons Row (Add Item & Single Add Negative / Return Row)
      if (!page.isSaved) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(ledgerColors.ledgerPaperShaded.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(
            onClick = { onAddPositiveRow?.invoke() },
            modifier = Modifier.testTag("add_item_button"),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = ledgerColors.inkNavy
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "+ Add Item",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = ledgerColors.inkNavy
            )
          }

          // Single dedicated button to add a negative row (as in normal paper bills)
          Surface(
            onClick = { onAddNegativeRow?.invoke() },
            shape = RoundedCornerShape(6.dp),
            color = ledgerColors.softRedLight,
            border = BorderStroke(1.dp, ledgerColors.softRed.copy(alpha = 0.5f)),
            modifier = Modifier.testTag("add_negative_row_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.RemoveCircleOutline,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = ledgerColors.softRed
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = "Add Return / Minus Item (−)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ledgerColors.softRed
              )
            }
          }
        }
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
              text = "PAGE ${page.pageNumber} SUB-TOTAL",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp,
              color = ledgerColors.inkNavy
            )
            val isNegative = page.pageTotal < 0
            Text(
              text = Formatters.formatCurrency(page.pageTotal, currencySymbol),
              fontFamily = FontFamily.Monospace,
              color = if (isNegative) ledgerColors.softRed else ledgerColors.ledgerGreen,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.testTag("page_${page.pageNumber}_total")
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
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
  showProductName: Boolean,
  isLocked: Boolean,
  isLastRow: Boolean,
  onNameChange: (String) -> Unit,
  onQtyChange: (String) -> Unit,
  onRateChange: (String) -> Unit,
  onDelete: () -> Unit,
  onNextFocus: () -> Unit
) {
  val ledgerColors = LocalLedgerColors.current

  val rowBackground = when {
    row.isReturn -> ledgerColors.softRedBg
    row.isRowFilled -> ledgerColors.ledgerPaperShaded.copy(alpha = 0.35f)
    else -> Color.Transparent
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(rowBackground)
      .padding(horizontal = 12.dp, vertical = 7.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (showProductName) {
      // Product Name Field
      Row(
        modifier = Modifier
          .weight(4.2f)
          .padding(end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (row.isReturn) {
          Surface(
            shape = RoundedCornerShape(3.dp),
            color = ledgerColors.softRed,
            modifier = Modifier.padding(end = 5.dp)
          ) {
            Text(
              text = "RET −",
              color = Color.White,
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }
        }

        Box(modifier = Modifier.weight(1f)) {
          if (row.productName.isEmpty() && !isLocked) {
            Text(
              text = if (row.isReturn) "Returned item name..." else if (isLastRow) "Add item name..." else "Item name",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = if (row.isReturn) ledgerColors.softRed.copy(alpha = 0.6f) else ledgerColors.placeholderColor,
                fontSize = 13.5.sp
              )
            )
          }
          BasicTextField(
            value = row.productName,
            onValueChange = onNameChange,
            readOnly = isLocked,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
              color = if (row.isReturn) ledgerColors.softRed else ledgerColors.charcoal,
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Medium
            ),
            cursorBrush = SolidColor(if (row.isReturn) ledgerColors.softRed else ledgerColors.stampAmber),
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
      }

      // Quantity Field (Monospace, Right-Aligned, Decimal Keyboard)
      Box(
        modifier = Modifier
          .weight(1.6f)
          .padding(horizontal = 2.dp),
        contentAlignment = Alignment.CenterEnd
      ) {
        if (row.quantityStr.isEmpty() && !isLocked) {
          Text(
            text = "0",
            style = LedgerNumeralRegular.copy(
              color = ledgerColors.placeholderColor,
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
            color = if (row.isReturn) ledgerColors.softRed else ledgerColors.charcoal,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
          ),
          cursorBrush = SolidColor(if (row.isReturn) ledgerColors.softRed else ledgerColors.stampAmber),
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
          .weight(1.8f)
          .padding(horizontal = 2.dp),
        contentAlignment = Alignment.CenterEnd
      ) {
        if (row.rateStr.isEmpty() && !isLocked) {
          Text(
            text = "0.00",
            style = LedgerNumeralRegular.copy(
              color = ledgerColors.placeholderColor,
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
            color = if (row.isReturn) ledgerColors.softRed else ledgerColors.charcoal,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
          ),
          cursorBrush = SolidColor(if (row.isReturn) ledgerColors.softRed else ledgerColors.stampAmber),
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
      val totalDisplay = when {
        row.hasCalculation && row.isReturn -> "-${Formatters.formatMoneyValue(kotlin.math.abs(row.total))}"
        row.hasCalculation && row.total > 0 -> Formatters.formatMoneyValue(row.total)
        row.hasCalculation && row.total == 0.0 -> "0.00"
        else -> "-"
      }

      Text(
        text = totalDisplay,
        style = LedgerNumeralRegular.copy(
          fontWeight = if (row.hasCalculation) FontWeight.Bold else FontWeight.Normal,
          fontSize = 13.5.sp,
          color = when {
            row.isReturn && row.hasCalculation -> ledgerColors.softRed
            row.total > 0 -> ledgerColors.ledgerGreen
            else -> ledgerColors.placeholderColor
          },
          textAlign = TextAlign.End
        ),
        modifier = Modifier
          .weight(2.6f)
          .padding(start = 2.dp, end = 2.dp)
          .testTag("row_total_$rowNumber")
      )
    } else {
      // Fast Qty x Rate Mode without product name
      // Item # Indicator
      Surface(
        shape = RoundedCornerShape(4.dp),
        color = if (row.isReturn) ledgerColors.softRedLight else ledgerColors.ledgerPaperShaded,
        modifier = Modifier
          .weight(1.4f)
          .border(0.8.dp, if (row.isReturn) ledgerColors.softRed else ledgerColors.ruledLine, RoundedCornerShape(4.dp))
      ) {
        Text(
          text = if (row.isReturn) "-#$rowNumber" else "#$rowNumber",
          style = LedgerNumeralRegular,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = if (row.isReturn) ledgerColors.softRed else ledgerColors.inkNavy,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
        )
      }

      // Quantity Field
      Box(
        modifier = Modifier
          .weight(2.8f)
          .padding(horizontal = 3.dp),
        contentAlignment = Alignment.CenterEnd
      ) {
        if (row.quantityStr.isEmpty() && !isLocked) {
          Text(
            text = if (isLastRow) "Enter qty" else "0",
            style = LedgerNumeralRegular.copy(
              color = ledgerColors.placeholderColor,
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
            color = if (row.isReturn) ledgerColors.softRed else ledgerColors.charcoal,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
          ),
          cursorBrush = SolidColor(if (row.isReturn) ledgerColors.softRed else ledgerColors.stampAmber),
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

      // Rate Field
      Box(
        modifier = Modifier
          .weight(2.8f)
          .padding(horizontal = 3.dp),
        contentAlignment = Alignment.CenterEnd
      ) {
        if (row.rateStr.isEmpty() && !isLocked) {
          Text(
            text = if (isLastRow) "Enter rate" else "0.00",
            style = LedgerNumeralRegular.copy(
              color = ledgerColors.placeholderColor,
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
            color = if (row.isReturn) ledgerColors.softRed else ledgerColors.charcoal,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
          ),
          cursorBrush = SolidColor(if (row.isReturn) ledgerColors.softRed else ledgerColors.stampAmber),
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

      // Total Amount
      val totalDisplay = when {
        row.hasCalculation && row.isReturn -> "-${Formatters.formatMoneyValue(kotlin.math.abs(row.total))}"
        row.hasCalculation && row.total > 0 -> Formatters.formatMoneyValue(row.total)
        row.hasCalculation && row.total == 0.0 -> "0.00"
        else -> "-"
      }

      Text(
        text = totalDisplay,
        style = LedgerNumeralRegular.copy(
          fontWeight = if (row.hasCalculation) FontWeight.Bold else FontWeight.Normal,
          fontSize = 13.5.sp,
          color = when {
            row.isReturn && row.hasCalculation -> ledgerColors.softRed
            row.total > 0 -> ledgerColors.ledgerGreen
            else -> ledgerColors.placeholderColor
          },
          textAlign = TextAlign.End
        ),
        modifier = Modifier
          .weight(3.2f)
          .padding(start = 2.dp, end = 2.dp)
          .testTag("row_total_$rowNumber")
      )
    }

    // Delete Action Row
    Box(
      modifier = Modifier.width(32.dp),
      contentAlignment = Alignment.CenterEnd
    ) {
      if (!isLocked && (row.isRowFilled || !isLastRow || row.isReturn)) {
        IconButton(
          onClick = onDelete,
          modifier = Modifier
            .size(28.dp)
            .testTag("delete_row_$rowNumber")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Delete Row",
            tint = if (row.isReturn) ledgerColors.softRed else ledgerColors.mutedCharcoal,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
