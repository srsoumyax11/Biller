package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LedgerTable
import com.example.ui.components.StickyGrossTotalBar
import com.example.ui.theme.LedgerNumeralMedium
import com.example.ui.theme.LocalLedgerColors
import com.example.ui.viewmodel.ActiveBillState
import com.example.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillEntryScreen(
  billState: ActiveBillState,
  snackbarMessage: String?,
  pageSavedAnimationIndex: Int?,
  onBackClick: () -> Unit,
  onSelectPage: (Int) -> Unit,
  onSavePage: () -> Unit,
  onAddNewPage: () -> Unit,
  onTogglePageLock: (Int) -> Unit,
  onUpdateRow: (rowIndex: Int, name: String?, qty: String?, rate: String?) -> Unit,
  onDeleteRow: (rowIndex: Int) -> Unit,
  onUndoDeleteRow: () -> Unit,
  onClearSnackbar: () -> Unit,
  onUpdateMetadata: (name: String, phone: String, invoice: String, dateMillis: Long, note: String, currency: String) -> Unit,
  onSaveDraft: () -> Unit,
  onFinishBill: () -> Unit,
  modifier: Modifier = Modifier
) {
  val ledgerColors = LocalLedgerColors.current
  val snackbarHostState = remember { SnackbarHostState() }
  var showMetadataDialog by remember { mutableStateOf(false) }

  // Handle snackbar messages
  LaunchedEffect(snackbarMessage) {
    if (snackbarMessage != null) {
      val isDeleteAction = snackbarMessage.contains("deleted", ignoreCase = true)
      val result = snackbarHostState.showSnackbar(
        message = snackbarMessage,
        actionLabel = if (isDeleteAction) "UNDO" else null,
        duration = SnackbarDuration.Short
      )
      if (result == SnackbarResult.ActionPerformed && isDeleteAction) {
        onUndoDeleteRow()
      }
      onClearSnackbar()
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("bill_entry_screen"),
    containerColor = ledgerColors.ledgerPaper,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      Surface(
        color = ledgerColors.inkNavy,
        shadowElevation = 4.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          // Top navigation & actions row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Surface(
                onClick = onBackClick,
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                  .size(32.dp)
                  .testTag("entry_back_button")
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }

              Text(
                text = "New Bill",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.White,
                letterSpacing = (-0.3).sp
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              // Edit Bill Metadata button
              IconButton(
                onClick = { showMetadataDialog = true },
                modifier = Modifier
                  .size(36.dp)
                  .testTag("edit_metadata_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = "Customer Details",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }

              // Save draft button
              IconButton(
                onClick = onSaveDraft,
                modifier = Modifier
                  .size(36.dp)
                  .testTag("save_draft_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Save,
                  contentDescription = "Save Draft",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // High Density Header Sub-strip: Customer Name & Inv #
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showMetadataDialog = true }
              .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
          ) {
            Column {
              Text(
                text = "CUSTOMER NAME",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color.White.copy(alpha = 0.6f)
              )
              val customerDisplay = if (billState.customerName.isNotBlank()) billState.customerName else "Walk-in Customer"
              Text(
                text = customerDisplay,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "INV #",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color.White.copy(alpha = 0.6f)
              )
              Text(
                text = billState.invoiceNumber,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                fontSize = 14.sp
              )
            }
          }
        }
      }
    },
    bottomBar = {
      val activePage = billState.activePage
      StickyGrossTotalBar(
        grossTotal = billState.grossTotal,
        totalPagesCount = billState.pages.size,
        activePageNumber = activePage.pageNumber,
        isCurrentPageSaved = activePage.isSaved,
        currencySymbol = billState.currencySymbol,
        onSavePage = {
          if (activePage.isSaved) {
            onAddNewPage()
          } else {
            onSavePage()
          }
        },
        onFinishBill = onFinishBill
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(bottom = 16.dp)
    ) {
      // Customer Info Strip
      item {
        CustomerInfoStrip(
          billState = billState,
          onEditClick = { showMetadataDialog = true }
        )
      }

      // Horizontal Page Selector Navigation
      item {
        PageSelectorTabs(
          pages = billState.pages,
          activePageIndex = billState.activePageIndex,
          currencySymbol = billState.currencySymbol,
          onSelectPage = onSelectPage,
          onAddPage = onAddNewPage
        )
      }

      // Active Page Ledger Table
      item {
        val activePage = billState.activePage
        val isAnimated = pageSavedAnimationIndex == billState.activePageIndex
        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
          LedgerTable(
            page = activePage,
            pageIndex = billState.activePageIndex,
            currencySymbol = billState.currencySymbol,
            isAnimatedStamp = isAnimated,
            onRowChange = onUpdateRow,
            onDeleteRow = onDeleteRow,
            onToggleLock = { onTogglePageLock(billState.activePageIndex) }
          )
        }
      }

      // Helpful Ledger Tip at bottom of scroll
      item {
        Text(
          text = "• Empty row is ready to type • New row generates automatically\n• Saved pages lock amounts and can be reopened anytime",
          style = MaterialTheme.typography.bodySmall,
          color = ledgerColors.mutedCharcoal,
          fontSize = 11.sp,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
      }
    }
  }

  // Metadata edit dialog
  if (showMetadataDialog) {
    CustomerMetadataDialog(
      initialName = billState.customerName,
      initialPhone = billState.phone,
      initialInvoiceNumber = billState.invoiceNumber,
      initialDateMillis = billState.dateMillis,
      initialNote = billState.note,
      initialCurrency = billState.currencySymbol,
      onDismiss = { showMetadataDialog = false },
      onSave = onUpdateMetadata
    )
  }
}

@Composable
private fun CustomerInfoStrip(
  billState: ActiveBillState,
  onEditClick: () -> Unit
) {
  val ledgerColors = LocalLedgerColors.current

  Surface(
    color = ledgerColors.ledgerPaperShaded,
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onEditClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = null,
          tint = ledgerColors.inkNavy,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        val nameText = if (billState.customerName.isNotBlank()) billState.customerName else "Add Customer Name"
        Text(
          text = nameText,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          color = if (billState.customerName.isNotBlank()) ledgerColors.charcoal else ledgerColors.stampAmber,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        if (billState.phone.isNotBlank()) {
          Text(
            text = " (${billState.phone})",
            style = MaterialTheme.typography.bodySmall,
            color = ledgerColors.mutedCharcoal,
            maxLines = 1
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = Formatters.formatDate(billState.dateMillis),
          style = MaterialTheme.typography.labelSmall,
          color = ledgerColors.mutedCharcoal
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          imageVector = Icons.Default.Edit,
          contentDescription = "Edit",
          tint = ledgerColors.mutedCharcoal,
          modifier = Modifier.size(14.dp)
        )
      }
    }
  }
}

@Composable
private fun PageSelectorTabs(
  pages: List<com.example.ui.viewmodel.UiPage>,
  activePageIndex: Int,
  currencySymbol: String,
  onSelectPage: (Int) -> Unit,
  onAddPage: () -> Unit
) {
  val ledgerColors = LocalLedgerColors.current
  val scrollState = rememberScrollState()

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(scrollState)
      .padding(horizontal = 12.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    pages.forEachIndexed { index, page ->
      val isSelected = index == activePageIndex
      FilterChip(
        selected = isSelected,
        onClick = { onSelectPage(index) },
        leadingIcon = {
          if (page.isSaved) {
            Icon(
              imageVector = Icons.Default.Bookmark,
              contentDescription = "Saved",
              tint = if (isSelected) ledgerColors.ledgerGreen else ledgerColors.mutedCharcoal,
              modifier = Modifier.size(14.dp)
            )
          }
        },
        label = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Page ${page.pageNumber}",
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = Formatters.formatCurrency(page.pageTotal, currencySymbol),
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = if (page.pageTotal > 0) ledgerColors.ledgerGreen else ledgerColors.mutedCharcoal
            )
          }
        },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = ledgerColors.ledgerPaperSurface,
          selectedLabelColor = ledgerColors.inkNavy,
          containerColor = ledgerColors.ledgerPaperShaded.copy(alpha = 0.5f)
        ),
        border = FilterChipDefaults.filterChipBorder(
          enabled = true,
          selected = isSelected,
          borderColor = if (isSelected) ledgerColors.stampAmber else ledgerColors.ruledLine,
          selectedBorderColor = ledgerColors.stampAmber,
          borderWidth = if (isSelected) 1.6.dp else 1.dp
        ),
        modifier = Modifier.testTag("page_tab_${page.pageNumber}")
      )
    }

    // Add New Page Button
    OutlinedButton(
      onClick = onAddPage,
      modifier = Modifier
        .height(34.dp)
        .testTag("add_page_tab_button"),
      shape = RoundedCornerShape(8.dp),
      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Add Page",
        modifier = Modifier.size(16.dp),
        tint = ledgerColors.stampAmber
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "New Page",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = ledgerColors.stampAmber
      )
    }
  }
}
