package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BillEntity
import com.example.ui.theme.LedgerNumeralMedium
import com.example.ui.theme.LocalLedgerColors
import com.example.util.Formatters

@Composable
fun HomeScreen(
  bills: List<BillEntity>,
  totalRevenue: Double?,
  billCount: Int,
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  onNewBillClick: () -> Unit,
  onBillClick: (Long) -> Unit,
  onEditBillClick: (Long) -> Unit,
  onDuplicateBillClick: (Long) -> Unit,
  onDeleteBillClick: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  val ledgerColors = LocalLedgerColors.current
  val context = LocalContext.current

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen"),
    containerColor = ledgerColors.ledgerPaper,
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = onNewBillClick,
        containerColor = ledgerColors.stampAmber,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
        icon = {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "New Bill",
            modifier = Modifier.size(24.dp)
          )
        },
        text = {
          Text(
            text = "NEW BILL",
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            letterSpacing = 1.sp
          )
        },
        modifier = Modifier.testTag("new_bill_fab")
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(bottom = 96.dp)
    ) {
      // Top App Bar & Header
      item {
        HomeHeaderSection(
          totalRevenue = totalRevenue,
          billCount = billCount
        )
      }

      // Search Bar
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search bills by customer, phone, invoice #...") },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = "Search", tint = ledgerColors.inkNavy)
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchChange("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Clear", tint = ledgerColors.mutedCharcoal)
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = ledgerColors.ledgerPaperSurface,
              unfocusedContainerColor = ledgerColors.ledgerPaperSurface,
              focusedBorderColor = ledgerColors.stampAmber,
              unfocusedBorderColor = ledgerColors.ruledLine
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("home_search_input")
          )
        }
      }

      // Past Bills Header
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (searchQuery.isEmpty()) "Past Billing Records" else "Search Results (${bills.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ledgerColors.inkNavy
          )

          if (bills.isNotEmpty()) {
            Text(
              text = "${bills.size} ${if (bills.size == 1) "bill" else "bills"}",
              style = MaterialTheme.typography.bodySmall,
              color = ledgerColors.mutedCharcoal
            )
          }
        }
      }

      // Bills List or Empty State
      if (bills.isEmpty()) {
        item {
          EmptyBillsState(
            isSearching = searchQuery.isNotEmpty(),
            onNewBillClick = onNewBillClick
          )
        }
      } else {
        items(
          items = bills,
          key = { it.id }
        ) { bill ->
          BillCardItem(
            bill = bill,
            onClick = { onBillClick(bill.id) },
            onEdit = { onEditBillClick(bill.id) },
            onDuplicate = { onDuplicateBillClick(bill.id) },
            onDelete = { onDeleteBillClick(bill.id) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun HomeHeaderSection(
  totalRevenue: Double?,
  billCount: Int
) {
  val ledgerColors = LocalLedgerColors.current

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(ledgerColors.inkNavy)
      .padding(horizontal = 20.dp, vertical = 18.dp)
  ) {
    // Title & Offline Status
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = ledgerColors.stampAmber,
          modifier = Modifier.size(36.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Calculate,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "BILL CALCULATOR",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 1.2.sp
          )
          Text(
            text = "Digital Billing Book & Ledger",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp
          )
        }
      }

      // Offline indicator chip
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.15f),
        modifier = Modifier.padding(2.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .background(Color(0xFF4ADE80), CircleShape)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Offline Mode",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Metrics Quick Card
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color.White.copy(alpha = 0.08f),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "TOTAL REVENUE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.7f),
            letterSpacing = 0.8.sp
          )
          Text(
            text = Formatters.formatCurrency(totalRevenue ?: 0.0),
            style = LedgerNumeralMedium,
            color = Color(0xFF4ADE80),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Box(
          modifier = Modifier
            .height(32.dp)
            .width(1.dp)
            .background(Color.White.copy(alpha = 0.15f))
        )

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "TOTAL BILLS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.7f),
            letterSpacing = 0.8.sp
          )
          Text(
            text = "$billCount",
            style = LedgerNumeralMedium,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
private fun BillCardItem(
  bill: BillEntity,
  onClick: () -> Unit,
  onEdit: () -> Unit,
  onDuplicate: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val ledgerColors = LocalLedgerColors.current
  var menuExpanded by remember { mutableStateOf(false) }

  ElevatedCard(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .testTag("bill_card_${bill.id}"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.elevatedCardColors(
      containerColor = ledgerColors.ledgerPaperSurface
    ),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.5.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, ledgerColors.ruledLine, RoundedCornerShape(10.dp))
        .padding(14.dp)
    ) {
      // Top Row: Invoice # & Date & Actions Menu
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = ledgerColors.ledgerPaperShaded,
            modifier = Modifier.border(0.8.dp, ledgerColors.ruledLineStrong, RoundedCornerShape(4.dp))
          ) {
            Text(
              text = bill.invoiceNumber.ifBlank { "INV-#${bill.id}" },
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = ledgerColors.inkNavy,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = Formatters.formatDate(bill.dateMillis),
            style = MaterialTheme.typography.bodySmall,
            color = ledgerColors.mutedCharcoal,
            fontSize = 11.sp
          )
        }

        Box {
          IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "Options",
              tint = ledgerColors.mutedCharcoal,
              modifier = Modifier.size(18.dp)
            )
          }

          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
          ) {
            DropdownMenuItem(
              text = { Text("Open & Edit") },
              leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
              onClick = {
                menuExpanded = false
                onEdit()
              }
            )
            DropdownMenuItem(
              text = { Text("Duplicate Bill") },
              leadingIcon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
              onClick = {
                menuExpanded = false
                onDuplicate()
              }
            )
            HorizontalDivider()
            DropdownMenuItem(
              text = { Text("Delete Bill", color = ledgerColors.softRed) },
              leadingIcon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = ledgerColors.softRed) },
              onClick = {
                menuExpanded = false
                onDelete()
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Middle Row: Customer Info & Gross Amount
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = if (bill.customerName.isNotBlank()) bill.customerName else "Walk-in Customer",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ledgerColors.inkNavy,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          if (bill.phone.isNotBlank()) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(top = 2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                tint = ledgerColors.mutedCharcoal,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = bill.phone,
                style = MaterialTheme.typography.bodySmall,
                color = ledgerColors.mutedCharcoal,
                fontSize = 12.sp
              )
            }
          }
        }

        // Amount
        Text(
          text = Formatters.formatCurrency(bill.grossTotal),
          style = LedgerNumeralMedium,
          color = ledgerColors.ledgerGreen,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }

      if (bill.note.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Note: ${bill.note}",
          style = MaterialTheme.typography.bodySmall,
          color = ledgerColors.mutedCharcoal,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
private fun EmptyBillsState(
  isSearching: Boolean,
  onNewBillClick: () -> Unit
) {
  val ledgerColors = LocalLedgerColors.current

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(40.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Surface(
      shape = CircleShape,
      color = ledgerColors.ledgerPaperShaded,
      modifier = Modifier
        .size(80.dp)
        .border(1.dp, ledgerColors.ruledLine, CircleShape)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = if (isSearching) Icons.Default.Search else Icons.Default.MenuBook,
          contentDescription = null,
          tint = ledgerColors.stampAmber,
          modifier = Modifier.size(40.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = if (isSearching) "No matching bills found" else "No bills yet",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = ledgerColors.inkNavy
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = if (isSearching) "Try searching for a different customer name, phone number, or invoice #"
      else "Start replacing your paper billing book. Enter items page by page with live totals.",
      style = MaterialTheme.typography.bodyMedium,
      color = ledgerColors.mutedCharcoal,
      fontSize = 13.sp,
      textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )

    if (!isSearching) {
      Spacer(modifier = Modifier.height(20.dp))
      Surface(
        onClick = onNewBillClick,
        shape = RoundedCornerShape(8.dp),
        color = ledgerColors.stampAmber,
        modifier = Modifier.testTag("create_first_bill_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Create First Bill", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
