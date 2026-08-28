package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BillDao
import com.example.data.model.BillEntity
import com.example.data.model.BillPageEntity
import com.example.data.model.BillRowEntity

@Database(
  entities = [BillEntity::class, BillPageEntity::class, BillRowEntity::class],
  version = 1,
  exportSchema = false
)
abstract class BillDatabase : RoomDatabase() {
  abstract fun billDao(): BillDao

  companion object {
    @Volatile
    private var INSTANCE: BillDatabase? = null

    fun getDatabase(context: Context): BillDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          BillDatabase::class.java,
          "bill_calculator_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
