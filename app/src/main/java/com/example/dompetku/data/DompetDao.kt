
package com.example.dompetku.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DompetDao {

    // Anggaran kategori
    @Query("SELECT * FROM budgets WHERE monthKey = :monthKey")
    fun observeBudgets(monthKey: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBudget(budget: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBudgets(budgets: List<BudgetEntity>)

    // Transaksi
    @Query(
        "SELECT * FROM transactions " +
        "WHERE monthKey = :monthKey ORDER BY dateMillis DESC"
    )
    fun observeTransactions(monthKey: String): Flow<List<TransactionEntity>>

    @Query(
        "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
        "WHERE monthKey = :monthKey AND type = 'INCOME'"
    )
    fun observeIncomeTotal(monthKey: String): Flow<Long>

    @Query(
        "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
        "WHERE monthKey = :monthKey AND type = 'EXPENSE'"
    )
    fun observeExpenseTotal(monthKey: String): Flow<Long>

    @Query(
        "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
        "WHERE monthKey = :monthKey AND category = :category"
    )
    fun observeCategoryTotal(
        monthKey: String,
        category: String
    ): Flow<Long>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    // Status cicilan
    @Query("SELECT * FROM installments WHERE monthKey = :monthKey LIMIT 1")
    fun observeInstallment(monthKey: String): Flow<InstallmentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveInstallment(installment: InstallmentEntity)

    // Laporan bulanan
    @Query("SELECT * FROM monthly_reports ORDER BY monthKey DESC")
    fun observeReports(): Flow<List<MonthlyReportEntity>>

    @Query("SELECT * FROM monthly_reports WHERE monthKey = :monthKey LIMIT 1")
    suspend fun getReport(monthKey: String): MonthlyReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReport(report: MonthlyReportEntity)

    // Pengaturan aplikasi
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun observeSettings(): Flow<AppSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)

    // Penutupan bulan
    @Query("SELECT * FROM budgets WHERE monthKey = :monthKey")
    suspend fun getBudgetsForMonth(monthKey: String): List<BudgetEntity>

    @Query("SELECT * FROM transactions WHERE monthKey = :monthKey")
    suspend fun getTransactionsForMonth(monthKey: String): List<TransactionEntity>

    @Query("SELECT * FROM installments WHERE monthKey = :monthKey LIMIT 1")
    suspend fun getInstallmentForMonth(monthKey: String): InstallmentEntity?

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): AppSettingsEntity?
}
