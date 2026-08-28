package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
  onSavePage: () -> Unit,
  onFinishBill: () -> Unit,
  modifier: Modifier = Modifier
) {
  val ledgerColors = LocalLedgerColors.current

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("sticky_gross_total_bar"),
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    color = ledgerColors.inkNavy,
    shadowElevation = 10.dp,
    tonalElevation = 6.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
      // Main Gross Total & Offline Status Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left: Circle Green Icon + Gross Total
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .background(ledgerColors.ledgerGreen, RoundedCornerShape(21.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }

          Column {
            Text(
              text = "GROSS TOTAL",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 10.sp,
              letterSpacing = 1.2.sp
            )

            AnimatedContent(
              targetState = grossTotal,
              transitionSpec = {
                slideInVertically { height -> height } togetherWith slideOutVertically { height -> -height }
              },
              label = "gross_total_anim"
            ) { total ->
              Text(
                text = Formatters.formatMoneyValue(total),
                style = LedgerNumeralLarge,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("gross_total_text")
              )
            }
          }
        }

        // Right: Offline indicator
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(5.dp),
          modifier = Modifier
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .background(ledgerColors.ledgerGreen, RoundedCornerShape(3.dp))
          )
          Text(
            text = "OFFLINE MODE",
            color = Color(0xFF4ADE80),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Save Page / Next Page Button
        Button(
          onClick = onSavePage,
          modifier = Modifier
            .weight(1.1f)
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
            text = if (isCurrentPageSaved) "New Page" else "SAVE PAGE",
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp,
            letterSpacing = 0.5.sp
          )
        }

        // Finish Bill / View Summary Button
        Surface(
          onClick = onFinishBill,
          modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .testTag("finish_bill_button"),
          shape = RoundedCornerShape(8.dp),
          color = Color.White.copy(alpha = 0.12f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.ReceiptLong,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = Color.White
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "FINISH BILL",
              fontWeight = FontWeight.Medium,
              fontSize = 12.sp,
              color = Color.White,
              letterSpacing = 0.8.sp
            )
          }
        }
      }
    }
  }
}
