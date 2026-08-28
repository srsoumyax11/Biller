package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "bills")
data class BillEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val customerName: String = "",
  val phone: String = "",
  val dateMillis: Long = System.currentTimeMillis(),
  val invoiceNumber: String = "",
  val note: String = "",
  val grossTotal: Double = 0.0,
  val isDraft: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
  tableName = "bill_pages",
  foreignKeys = [
    ForeignKey(
      entity = BillEntity::class,
      parentColumns = ["id"],
      childColumns = ["billId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["billId"])]
)
data class BillPageEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val billId: Long = 0,
  val pageNumber: Int = 1,
  val pageTotal: Double = 0.0,
  val isSaved: Boolean = false
)

@Entity(
  tableName = "bill_rows",
  foreignKeys = [
    ForeignKey(
      entity = BillPageEntity::class,
      parentColumns = ["id"],
      childColumns = ["pageId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["pageId"])]
)
data class BillRowEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val pageId: Long = 0,
  val productName: String = "",
  val quantity: Double? = null,
  val rate: Double? = null,
  val total: Double = 0.0,
  val orderIndex: Int = 0
)

data class PageWithRows(
  @Embedded val page: BillPageEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "pageId"
  )
  val rows: List<BillRowEntity>
)

data class BillWithPagesAndRows(
  @Embedded val bill: BillEntity,
  @Relation(
    entity = BillPageEntity::class,
    parentColumn = "id",
    entityColumn = "billId"
  )
  val pagesWithRows: List<PageWithRows>
)
