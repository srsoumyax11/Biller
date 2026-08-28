package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.BillEntryScreen
import com.example.ui.screens.BillHistoryDetailScreen
import com.example.ui.screens.BillSummaryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BillViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        BillCalculatorApp()
      }
    }
  }
}

@Composable
fun BillCalculatorApp(
  viewModel: BillViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val bills by viewModel.billsList.collectAsState()
  val totalRevenue by viewModel.totalRevenue.collectAsState()
  val billCount by viewModel.billCount.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val activeBillState by viewModel.activeBillState.collectAsState()
  val snackbarMessage by viewModel.snackbarMessage.collectAsState()
  val pageSavedAnim by viewModel.pageSavedAnimation.collectAsState()

  // Handle system back navigation
  BackHandler(enabled = currentScreen !is Screen.Home) {
    when (currentScreen) {
      is Screen.BillEntry -> {
        viewModel.saveBillToDatabase(autoSave = true)
        viewModel.navigateTo(Screen.Home)
      }
      is Screen.BillSummary -> {
        viewModel.navigateTo(Screen.BillEntry)
      }
      is Screen.HistoryDetail -> {
        viewModel.navigateTo(Screen.Home)
      }
      Screen.Home -> {}
    }
  }

  Surface(modifier = Modifier.fillMaxSize()) {
    AnimatedContent(
      targetState = currentScreen,
      transitionSpec = {
        when {
          initialState is Screen.Home && targetState is Screen.BillEntry ->
            (slideInHorizontally { width -> width } + fadeIn()) togetherWith (slideOutHorizontally { width -> -width } + fadeOut())
          initialState is Screen.BillEntry && targetState is Screen.BillSummary ->
            (slideInHorizontally { width -> width } + fadeIn()) togetherWith (slideOutHorizontally { width -> -width } + fadeOut())
          targetState is Screen.Home ->
            (slideInHorizontally { width -> -width } + fadeIn()) togetherWith (slideOutHorizontally { width -> width } + fadeOut())
          else -> fadeIn() togetherWith fadeOut()
        }
      },
      label = "screen_navigation_anim"
    ) { screen ->
      when (screen) {
        Screen.Home -> {
          HomeScreen(
            bills = bills,
            totalRevenue = totalRevenue,
            billCount = billCount,
            searchQuery = searchQuery,
            onSearchChange = { viewModel.setSearchQuery(it) },
            onNewBillClick = { viewModel.startNewBill() },
            onBillClick = { billId -> viewModel.loadBillForViewingOrEditing(billId, editMode = false) },
            onEditBillClick = { billId -> viewModel.loadBillForViewingOrEditing(billId, editMode = true) },
            onDuplicateBillClick = { billId -> viewModel.duplicateBill(billId) },
            onDeleteBillClick = { billId -> viewModel.deleteBill(billId) }
          )
        }

        Screen.BillEntry -> {
          BillEntryScreen(
            billState = activeBillState,
            snackbarMessage = snackbarMessage,
            pageSavedAnimationIndex = pageSavedAnim,
            onBackClick = {
              viewModel.saveBillToDatabase(autoSave = true)
              viewModel.navigateTo(Screen.Home)
            },
            onSelectPage = { viewModel.selectPage(it) },
            onSavePage = { viewModel.saveCurrentPageAndAddNew() },
            onAddNewPage = { viewModel.addNewPageDirectly() },
            onTogglePageLock = { viewModel.togglePageLock(it) },
            onUpdateRow = { rowIndex, name, qty, rate ->
              viewModel.updateRow(
                pageIndex = activeBillState.activePageIndex,
                rowIndex = rowIndex,
                productName = name,
                quantityStr = qty,
                rateStr = rate
              )
            },
            onDeleteRow = { rowIndex ->
              viewModel.deleteRow(pageIndex = activeBillState.activePageIndex, rowIndex = rowIndex)
            },
            onUndoDeleteRow = { viewModel.undoDeleteRow() },
            onClearSnackbar = { viewModel.clearSnackbarMessage() },
            onUpdateMetadata = { name, phone, invoice, date, note, curr ->
              viewModel.updateCustomerMetadata(name, phone, invoice, date, note)
              viewModel.setCurrencySymbol(curr)
            },
            onSaveDraft = {
              viewModel.saveBillToDatabase(autoSave = false)
            },
            onFinishBill = {
              viewModel.saveBillToDatabase(autoSave = false) {
                viewModel.navigateTo(Screen.BillSummary)
              }
            }
          )
        }

        Screen.BillSummary -> {
          BillSummaryScreen(
            billState = activeBillState,
            onBackClick = { viewModel.navigateTo(Screen.BillEntry) },
            onEditBillClick = { viewModel.navigateTo(Screen.BillEntry) },
            onHomeClick = { viewModel.navigateTo(Screen.Home) }
          )
        }

        is Screen.HistoryDetail -> {
          BillHistoryDetailScreen(
            billState = activeBillState,
            onBackClick = { viewModel.navigateTo(Screen.Home) },
            onEditClick = { viewModel.loadBillForViewingOrEditing(screen.billId, editMode = true) },
            onDuplicateClick = { viewModel.duplicateBill(screen.billId) },
            onDeleteClick = { viewModel.deleteBill(screen.billId) }
          )
        }
      }
    }
  }
}
