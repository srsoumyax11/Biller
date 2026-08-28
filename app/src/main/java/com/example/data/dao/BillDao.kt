package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.BillEntity
import com.example.data.model.BillPageEntity
import com.example.data.model.BillRowEntity
import com.example.data.model.BillWithPagesAndRows
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {

  @Query("SELECT * FROM bills ORDER BY dateMillis DESC, id DESC")
  fun getAllBillsFlow(): Flow<List<BillEntity>>

  @Query("""
    SELECT * FROM bills 
    WHERE customerName LIKE '%' || :query || '%' 
       OR invoiceNumber LIKE '%' || :query || '%' 
       OR phone LIKE '%' || :query || '%'
    ORDER BY dateMillis DESC, id DESC
  """)
  fun searchBillsFlow(query: String): Flow<List<BillEntity>>

  @Transaction
  @Query("SELECT * FROM bills WHERE id = :billId")
  fun getBillWithDetailsFlow(billId: Long): Flow<BillWithPagesAndRows?>

  @Transaction
  @Query("SELECT * FROM bills WHERE id = :billId")
  suspend fun getBillWithDetails(billId: Long): BillWithPagesAndRows?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBill(bill: BillEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPage(page: BillPageEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRows(rows: List<BillRowEntity>): List<Long>

  @Query("DELETE FROM bill_pages WHERE billId = :billId")
  suspend fun deletePagesForBill(billId: Long)

  @Query("DELETE FROM bills WHERE id = :billId")
  suspend fun deleteBillById(billId: Long)

  @Query("SELECT COUNT(*) FROM bills")
  fun getBillCountFlow(): Flow<Int>

  @Query("SELECT SUM(grossTotal) FROM bills")
  fun getTotalRevenueFlow(): Flow<Double?>

  @Transaction
  suspend fun saveFullBill(
    bill: BillEntity,
    pages: List<Pair<BillPageEntity, List<BillRowEntity>>>
  ): Long {
    val billId = insertBill(bill)
    // Clear old pages and rows if updating existing bill
    deletePagesForBill(billId)

    for ((pageEntity, rowsList) in pages) {
      val savedPageEntity = pageEntity.copy(billId = billId)
      val pageId = insertPage(savedPageEntity)
      val rowsToInsert = rowsList.mapIndexed { idx, row ->
        row.copy(pageId = pageId, orderIndex = idx)
      }
      insertRows(rowsToInsert)
    }

    return billId
  }
}
