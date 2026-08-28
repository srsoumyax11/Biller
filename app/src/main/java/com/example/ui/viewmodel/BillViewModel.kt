package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.BillDatabase
import com.example.data.model.BillEntity
import com.example.data.model.BillPageEntity
import com.example.data.model.BillRowEntity
import com.example.data.repository.BillRepository
import com.example.util.Formatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class UiRow(
  val id: String = UUID.randomUUID().toString(),
  val productName: String = "",
  val quantityStr: String = "",
  val rateStr: String = ""
) {
  val quantity: Double? get() = Formatters.parseDecimal(quantityStr)
  val rate: Double? get() = Formatters.parseDecimal(rateStr)
  val total: Double get() {
    val q = quantity
    val r = rate
    return if (q != null && r != null) q * r else 0.0
  }
  val hasCalculation: Boolean get() = quantity != null && rate != null
  val isRowFilled: Boolean get() = productName.isNotBlank() || quantityStr.isNotBlank() || rateStr.isNotBlank()
}

data class UiPage(
  val id: String = UUID.randomUUID().toString(),
  val pageNumber: Int = 1,
  val isSaved: Boolean = false,
  val rows: List<UiRow> = listOf(UiRow())
) {
  val pageTotal: Double get() = rows.sumOf { it.total }
}

data class ActiveBillState(
  val billId: Long = 0L,
  val customerName: String = "",
  val phone: String = "",
  val dateMillis: Long = System.currentTimeMillis(),
  val invoiceNumber: String = Formatters.generateInvoiceNumber(),
  val note: String = "",
  val pages: List<UiPage> = listOf(UiPage(pageNumber = 1)),
  val activePageIndex: Int = 0,
  val currencySymbol: String = "₹",
  val isSavedToDb: Boolean = false
) {
  val grossTotal: Double get() = pages.sumOf { it.pageTotal }
  val activePage: UiPage get() = pages.getOrElse(activePageIndex) { pages.first() }
  val totalItemCount: Int get() = pages.sumOf { p -> p.rows.count { it.isRowFilled } }
}

sealed interface Screen {
  data object Home : Screen
  data object BillEntry : Screen
  data object BillSummary : Screen
  data class HistoryDetail(val billId: Long) : Screen
}

data class DeletedRowBackup(
  val pageIndex: Int,
  val rowIndex: Int,
  val row: UiRow
)

class BillViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: BillRepository = BillRepository(
    BillDatabase.getDatabase(application).billDao()
  )

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  val billsList: StateFlow<List<BillEntity>> = _searchQuery
    .flatMapLatest { query -> repository.searchBills(query) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalRevenue: StateFlow<Double?> = repository.totalRevenue
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val billCount: StateFlow<Int> = repository.billCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  private val _activeBillState = MutableStateFlow(ActiveBillState())
  val activeBillState: StateFlow<ActiveBillState> = _activeBillState.asStateFlow()

  private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  // For undoing deleted row or page
  private val _lastDeletedRow = MutableStateFlow<DeletedRowBackup?>(null)
  val lastDeletedRow: StateFlow<DeletedRowBackup?> = _lastDeletedRow.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  private val _pageSavedAnimation = MutableStateFlow<Int?>(null)
  val pageSavedAnimation: StateFlow<Int?> = _pageSavedAnimation.asStateFlow()

  fun navigateTo(screen: Screen) {
    _currentScreen.value = screen
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun clearSnackbarMessage() {
    _snackbarMessage.value = null
  }

  fun startNewBill(customerName: String = "", phone: String = "", note: String = "") {
    _activeBillState.value = ActiveBillState(
      billId = 0L,
      customerName = customerName,
      phone = phone,
      dateMillis = System.currentTimeMillis(),
      invoiceNumber = Formatters.generateInvoiceNumber(),
      note = note,
      pages = listOf(UiPage(pageNumber = 1, rows = listOf(UiRow()))),
      activePageIndex = 0,
      currencySymbol = _activeBillState.value.currencySymbol
    )
    _currentScreen.value = Screen.BillEntry
  }

  fun updateCustomerMetadata(
    customerName: String,
    phone: String,
    invoiceNumber: String,
    dateMillis: Long,
    note: String
  ) {
    _activeBillState.update { current ->
      current.copy(
        customerName = customerName,
        phone = phone,
        invoiceNumber = invoiceNumber,
        dateMillis = dateMillis,
        note = note
      )
    }
  }

  fun setCurrencySymbol(symbol: String) {
    _activeBillState.update { it.copy(currencySymbol = symbol) }
  }

  fun selectPage(pageIndex: Int) {
    if (pageIndex in _activeBillState.value.pages.indices) {
      _activeBillState.update { it.copy(activePageIndex = pageIndex) }
    }
  }

  fun updateRow(
    pageIndex: Int,
    rowIndex: Int,
    productName: String? = null,
    quantityStr: String? = null,
    rateStr: String? = null
  ) {
    _activeBillState.update { state ->
      val pages = state.pages.toMutableList()
      if (pageIndex !in pages.indices) return@update state

      val page = pages[pageIndex]
      val rows = page.rows.toMutableList()
      if (rowIndex !in rows.indices) return@update state

      val currentRow = rows[rowIndex]
      val updatedRow = currentRow.copy(
        productName = productName ?: currentRow.productName,
        quantityStr = quantityStr ?: currentRow.quantityStr,
        rateStr = rateStr ?: currentRow.rateStr
      )
      rows[rowIndex] = updatedRow

      // Auto-append row if user started typing in the last row
      val isLastRow = rowIndex == rows.size - 1
      if (isLastRow && updatedRow.isRowFilled) {
        rows.add(UiRow())
      }

      pages[pageIndex] = page.copy(rows = rows)
      state.copy(pages = pages)
    }
  }

  fun deleteRow(pageIndex: Int, rowIndex: Int) {
    _activeBillState.update { state ->
      val pages = state.pages.toMutableList()
      if (pageIndex !in pages.indices) return@update state

      val page = pages[pageIndex]
      val rows = page.rows.toMutableList()
      if (rowIndex !in rows.indices) return@update state

      val removedRow = rows.removeAt(rowIndex)
      // Save for Undo
      _lastDeletedRow.value = DeletedRowBackup(pageIndex, rowIndex, removedRow)
      _snackbarMessage.value = "Row deleted"

      // Always ensure at least one row exists
      if (rows.isEmpty()) {
        rows.add(UiRow())
      }

      pages[pageIndex] = page.copy(rows = rows)
      state.copy(pages = pages)
    }
  }

  fun undoDeleteRow() {
    val backup = _lastDeletedRow.value ?: return
    _activeBillState.update { state ->
      val pages = state.pages.toMutableList()
      if (backup.pageIndex !in pages.indices) return@update state

      val page = pages[backup.pageIndex]
      val rows = page.rows.toMutableList()
      val insertIndex = backup.rowIndex.coerceAtMost(rows.size)
      rows.add(insertIndex, backup.row)

      pages[backup.pageIndex] = page.copy(rows = rows)
      state.copy(pages = pages)
    }
    _lastDeletedRow.value = null
    _snackbarMessage.value = "Row restored"
  }

  fun saveCurrentPageAndAddNew() {
    _activeBillState.update { state ->
      val pages = state.pages.toMutableList()
      val activeIdx = state.activePageIndex
      if (activeIdx in pages.indices) {
        val currentPage = pages[activeIdx]
        // Clean trailing empty rows before saving page, keep at least filled rows or one empty
        val cleanedRows = currentPage.rows.filter { it.isRowFilled }.ifEmpty { listOf(UiRow()) }
        pages[activeIdx] = currentPage.copy(isSaved = true, rows = cleanedRows)
      }

      // Check if there is already an unlocked next page
      val nextPageIndex = activeIdx + 1
      if (nextPageIndex < pages.size) {
        state.copy(pages = pages, activePageIndex = nextPageIndex)
      } else {
        // Create new page
        val newPageNumber = pages.size + 1
        pages.add(UiPage(pageNumber = newPageNumber, isSaved = false, rows = listOf(UiRow())))
        state.copy(pages = pages, activePageIndex = pages.size - 1)
      }
    }

    _pageSavedAnimation.value = _activeBillState.value.activePageIndex
    _snackbarMessage.value = "Page saved • New ledger page opened"
    saveBillToDatabase(autoSave = true)
  }

  fun togglePageLock(pageIndex: Int) {
    _activeBillState.update { state ->
      val pages = state.pages.toMutableList()
      if (pageIndex in pages.indices) {
        val page = pages[pageIndex]
        val newSavedState = !page.isSaved
        val rows = page.rows.toMutableList()
        if (!newSavedState && (rows.isEmpty() || rows.last().isRowFilled)) {
          // If reopening, make sure trailing empty row is ready
          rows.add(UiRow())
        }
        pages[pageIndex] = page.copy(isSaved = newSavedState, rows = rows)
      }
      state.copy(pages = pages, activePageIndex = pageIndex)
    }
  }

  fun addNewPageDirectly() {
    _activeBillState.update { state ->
      val pages = state.pages.toMutableList()
      val newPageNumber = pages.size + 1
      pages.add(UiPage(pageNumber = newPageNumber, isSaved = false, rows = listOf(UiRow())))
      state.copy(pages = pages, activePageIndex = pages.size - 1)
    }
  }

  fun saveBillToDatabase(autoSave: Boolean = false, onComplete: ((Long) -> Unit)? = null) {
    val state = _activeBillState.value
    viewModelScope.launch {
      val billEntity = BillEntity(
        id = state.billId,
        customerName = state.customerName.trim(),
        phone = state.phone.trim(),
        dateMillis = state.dateMillis,
        invoiceNumber = state.invoiceNumber.ifBlank { Formatters.generateInvoiceNumber() },
        note = state.note.trim(),
        grossTotal = state.grossTotal,
        isDraft = false,
        updatedAt = System.currentTimeMillis()
      )

      val pageList = state.pages.map { page ->
        val pageEntity = BillPageEntity(
          billId = state.billId,
          pageNumber = page.pageNumber,
          pageTotal = page.pageTotal,
          isSaved = page.isSaved
        )
        val rowEntities = page.rows.filter { it.isRowFilled }.mapIndexed { idx, row ->
          BillRowEntity(
            productName = row.productName.trim(),
            quantity = row.quantity,
            rate = row.rate,
            total = row.total,
            orderIndex = idx
          )
        }
        Pair(pageEntity, rowEntities)
      }

      val savedId = repository.saveBill(billEntity, pageList)
      _activeBillState.update { it.copy(billId = savedId, isSavedToDb = true) }
      if (!autoSave) {
        _snackbarMessage.value = "Bill saved successfully"
      }
      onComplete?.invoke(savedId)
    }
  }

  fun loadBillForViewingOrEditing(billId: Long, editMode: Boolean = false) {
    viewModelScope.launch {
      val billDetails = repository.getBillDetails(billId)
      if (billDetails != null) {
        val loadedPages = billDetails.pagesWithRows
          .sortedBy { it.page.pageNumber }
          .map { pageWithRows ->
            val rows = pageWithRows.rows
              .sortedBy { it.orderIndex }
              .map { r ->
                UiRow(
                  id = UUID.randomUUID().toString(),
                  productName = r.productName,
                  quantityStr = r.quantity?.let { Formatters.formatQuantity(it) } ?: "",
                  rateStr = r.rate?.let { Formatters.formatMoneyValue(it) } ?: ""
                )
              }.toMutableList()

            if (editMode && (rows.isEmpty() || rows.last().isRowFilled)) {
              rows.add(UiRow())
            }

            UiPage(
              id = UUID.randomUUID().toString(),
              pageNumber = pageWithRows.page.pageNumber,
              isSaved = pageWithRows.page.isSaved,
              rows = rows.ifEmpty { listOf(UiRow()) }
            )
          }.ifEmpty {
            listOf(UiPage(pageNumber = 1, rows = listOf(UiRow())))
          }

        _activeBillState.value = ActiveBillState(
          billId = billDetails.bill.id,
          customerName = billDetails.bill.customerName,
          phone = billDetails.bill.phone,
          dateMillis = billDetails.bill.dateMillis,
          invoiceNumber = billDetails.bill.invoiceNumber,
          note = billDetails.bill.note,
          pages = loadedPages,
          activePageIndex = 0,
          currencySymbol = _activeBillState.value.currencySymbol,
          isSavedToDb = true
        )

        if (editMode) {
          _currentScreen.value = Screen.BillEntry
        } else {
          _currentScreen.value = Screen.HistoryDetail(billId)
        }
      }
    }
  }

  fun duplicateBill(billId: Long) {
    viewModelScope.launch {
      val billDetails = repository.getBillDetails(billId)
      if (billDetails != null) {
        val duplicatedPages = billDetails.pagesWithRows
          .sortedBy { it.page.pageNumber }
          .map { pwr ->
            val rows = pwr.rows.sortedBy { it.orderIndex }.map { r ->
              UiRow(
                id = UUID.randomUUID().toString(),
                productName = r.productName,
                quantityStr = r.quantity?.let { Formatters.formatQuantity(it) } ?: "",
                rateStr = r.rate?.let { Formatters.formatMoneyValue(it) } ?: ""
              )
            }.toMutableList()
            rows.add(UiRow())
            UiPage(
              id = UUID.randomUUID().toString(),
              pageNumber = pwr.page.pageNumber,
              isSaved = false,
              rows = rows
            )
          }

        _activeBillState.value = ActiveBillState(
          billId = 0L,
          customerName = billDetails.bill.customerName,
          phone = billDetails.bill.phone,
          dateMillis = System.currentTimeMillis(),
          invoiceNumber = Formatters.generateInvoiceNumber(),
          note = billDetails.bill.note,
          pages = duplicatedPages.ifEmpty { listOf(UiPage(pageNumber = 1)) },
          activePageIndex = 0,
          currencySymbol = _activeBillState.value.currencySymbol
        )
        _currentScreen.value = Screen.BillEntry
        _snackbarMessage.value = "Bill duplicated as new draft"
      }
    }
  }

  fun deleteBill(billId: Long) {
    viewModelScope.launch {
      repository.deleteBill(billId)
      _snackbarMessage.value = "Bill deleted"
      if (_currentScreen.value is Screen.HistoryDetail) {
        _currentScreen.value = Screen.Home
      }
    }
  }
}
