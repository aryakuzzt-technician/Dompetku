package com.example.dompetku.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BudgetEntity::class,
        TransactionEntity::class,
        InstallmentEntity::class,
        MonthlyReportEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DompetDatabase : RoomDatabase() {

    abstract fun dompetDao(): DompetDao

    companion object {
        @Volatile
        private var INSTANCE: DompetDatabase? = null

        fun getDatabase(context: Context): DompetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DompetDatabase::class.java,
                    "dompetku_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
