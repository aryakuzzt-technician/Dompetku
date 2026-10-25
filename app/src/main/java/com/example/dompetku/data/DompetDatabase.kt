
package com.example.dompetku.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        BudgetEntity::class,
        TransactionEntity::class,
        InstallmentEntity::class,
        MonthlyReportEntity::class,
        AppSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class DompetDatabase : RoomDatabase() {

    abstract fun dompetDao(): DompetDao

    companion object {
        @Volatile
        private var INSTANCE: DompetDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Migrasi tabel anggaran ke struktur dengan ID otomatis.
                db.execSQL("ALTER TABLE budgets RENAME TO budgets_old")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS budgets (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        category TEXT NOT NULL,
                        amount INTEGER NOT NULL,
                        percentage REAL NOT NULL,
                        monthKey TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO budgets (category, amount, percentage, monthKey)
                    SELECT category, amount, percentage, monthKey
                    FROM budgets_old
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE budgets_old")

                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS index_budgets_monthKey_category
                    ON budgets (monthKey, category)
                    """.trimIndent()
                )

                // Cicilan disimpan terpisah untuk setiap bulan.
                db.execSQL("ALTER TABLE installments RENAME TO installments_old")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS installments (
                        id INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        amount INTEGER NOT NULL,
                        isPaid INTEGER NOT NULL,
                        monthKey TEXT NOT NULL,
                        PRIMARY KEY(id, monthKey)
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO installments (id, title, amount, isPaid, monthKey)
                    SELECT id, title, amount, isPaid, monthKey
                    FROM installments_old
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE installments_old")

                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_transactions_monthKey ON transactions (monthKey)"
                )

                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_transactions_category_monthKey ON transactions (category, monthKey)"
                )
            }
        }

        fun getDatabase(context: Context): DompetDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DompetDatabase::class.java,
                    "dompetku_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
