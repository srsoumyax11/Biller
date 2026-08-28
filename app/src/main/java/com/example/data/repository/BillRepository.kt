package com.example.data.repository

import com.example.data.dao.BillDao
import com.example.data.model.BillEntity
import com.example.data.model.BillPageEntity
import com.example.data.model.BillRowEntity
import com.example.data.model.BillWithPagesAndRows
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BillRepository(private val billDao: BillDao) {

  val allBills: Flow<List<BillEntity>> = billDao.getAllBillsFlow()
  val totalRevenue: Flow<Double?> = billDao.getTotalRevenueFlow()
  val billCount: Flow<Int> = billDao.getBillCountFlow()

  fun searchBills(query: String): Flow<List<BillEntity>> {
    return if (query.isBlank()) {
      billDao.getAllBillsFlow()
    } else {
      billDao.searchBillsFlow(query.trim())
    }
  }

  fun getBillDetailsFlow(billId: Long): Flow<BillWithPagesAndRows?> {
    return billDao.getBillWithDetailsFlow(billId)
  }

  suspend fun getBillDetails(billId: Long): BillWithPagesAndRows? {
    return withContext(Dispatchers.IO) {
      billDao.getBillWithDetails(billId)
    }
  }

  suspend fun saveBill(
    bill: BillEntity,
    pages: List<Pair<BillPageEntity, List<BillRowEntity>>>
  ): Long {
    return withContext(Dispatchers.IO) {
      billDao.saveFullBill(bill, pages)
    }
  }

  suspend fun deleteBill(billId: Long) {
    withContext(Dispatchers.IO) {
      billDao.deleteBillById(billId)
    }
  }
}
