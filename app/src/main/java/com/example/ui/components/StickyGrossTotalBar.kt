package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LedgerNumeralLarge
import com.example.ui.theme.LocalLedgerColors
import com.example.util.Formatters

@Composable
fun StickyGrossTotalBar(
  grossTotal: Double,
  totalPagesCount: Int,
  activePageNumber: Int,
  isCurrentPageSaved: Boolean,
  currencySymbol: String,
  deductionAmount: Double = 0.0,
  claimedTotal: Double = 0.0,
  isExactMatch: Boolean = false,
  isOvercharged: Boolean = false,
  isUndercharged: Boolean = false,
  discrepancy: Double = 0.0,
  onOpenAuditDialog: () -> Unit = {},
  onSavePage: () -> Unit,
  onFinishBill: () -> Unit,
  modifier: Modifier = Modifier
) {
  val ledgerColors = LocalLedgerColors.current
  val hasDeduction = deductionAmount > 0.0
  val netPayable = (grossTotal - deductionAmount).coerceAtLeast(0.0)

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("sticky_gross_total_bar"),
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    color = ledgerColors.headerSurface,
    shadowElevation = 10.dp,
    tonalElevation = 0.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      // Main Gross/Net Total & Verification Status Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left: Circle Icon + Verified Total
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f, fill = false)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .background(ledgerColors.ledgerGreen, RoundedCornerShape(19.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }

          Column {
            Text(
              text = if (hasDeduction) "NET PAYABLE ($currencySymbol)" else "ACTUAL VERIFIED TOTAL ($currencySymbol)",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = ledgerColors.headerContentMuted,
              fontSize = 10.5.sp,
              letterSpacing = 0.8.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            AnimatedContent(
              targetState = if (hasDeduction) netPayable else grossTotal,
              transitionSpec = {
                slideInVertically { height -> height } togetherWith slideOutVertically { height -> -height }
              },
              label = "gross_total_anim"
            ) { total ->
              Text(
                text = Formatters.formatMoneyValue(total),
                style = LedgerNumeralLarge,
                color = ledgerColors.headerContent,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.testTag("gross_total_text")
              )
            }
          }
        }

        // Right: Verification Result Chip or Compare Target Button
        if (claimedTotal > 0.0) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = when {
              isExactMatch -> Color(0xFF1B4D3E)
              isOvercharged -> Color(0xFF7F1D1D)
              else -> Color(0xFF374151)
            },
            modifier = Modifier
              .clickable { onOpenAuditDialog() }
              .testTag("audit_discrepancy_chip")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(5.dp),
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = if (isExactMatch) Icons.Default.CheckCircle else Icons.Default.WarningAmber,
                contentDescription = null,
                tint = if (isExactMatch) Color(0xFF4ADE80) else Color(0xFFFCA5A5),
                modifier = Modifier.size(15.dp)
              )
              Text(
                text = when {
                  isExactMatch -> "MATCH ✅"
                  isOvercharged -> "OVER +${Formatters.formatCurrency(discrepancy, currencySymbol)}"
                  else -> "DIFF ${Formatters.formatCurrency(discrepancy, currencySymbol)}"
                },
                color = if (isExactMatch) Color(0xFF4ADE80) else Color(0xFFFCA5A5),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
              )
            }
          }
        } else {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.12f),
            modifier = Modifier
              .clickable { onOpenAuditDialog() }
              .testTag("compare_bill_prompt_chip")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
              Text(
                text = "+ Compare Bill",
                color = Color(0xFFE2E8F0),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Save Page / Next Page Button
        Button(
          onClick = onSavePage,
          modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .testTag("save_page_action_button"),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = ledgerColors.stampAmber,
            contentColor = Color.White
          )
        ) {
          Icon(
            imageVector = if (isCurrentPageSaved) Icons.Default.Add else Icons.Default.BookmarkBorder,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isCurrentPageSaved) "Next Page" else "Save Page",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 0.3.sp
          )
        }

        // View Summary & Audit Report Button
        Surface(
          onClick = onFinishBill,
          modifier = Modifier
            .weight(1.1f)
            .height(42.dp)
            .testTag("finish_bill_button"),
          shape = RoundedCornerShape(8.dp),
          color = Color.White.copy(alpha = 0.16f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.ReceiptLong,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = ledgerColors.headerContent
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Audit Report",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = ledgerColors.headerContent,
              letterSpacing = 0.3.sp
            )
          }
        }
      }
    }
  }
}
