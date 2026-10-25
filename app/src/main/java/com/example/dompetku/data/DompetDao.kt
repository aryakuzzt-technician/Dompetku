
package com.example.dompetku.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DompetDao {

    // =========================
    // ANGGARAN
    // =========================

    @Query(
        "SELECT * FROM budgets " +
        "WHERE monthKey = :monthKey ORDER BY category ASC"
    )
    fun observeBudgets(monthKey: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE monthKey = :monthKey")
    suspend fun getBudgets(monthKey: String): List<BudgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<BudgetEntity>)

    // =========================
    // TRANSAKSI
    // =========================

    @Query(
        "SELECT * FROM transactions " +
        "WHERE monthKey = :monthKey ORDER BY dateMillis DESC"
    )
    fun observeTransactions(
        monthKey: String
    ): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE monthKey = :monthKey")
    suspend fun getTransactions(monthKey: String): List<TransactionEntity>

    @Query(
        "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
        "WHERE monthKey = :monthKey AND type = 'INCOME'"
    )
    fun observeIncome(monthKey: String): Flow<Long>

    @Query(
        "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
        "WHERE monthKey = :monthKey AND type = 'EXPENSE'"
    )
    fun observeExpenses(monthKey: String): Flow<Long>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    // =========================
    // CICILAN
    // =========================

    @Query(
        "SELECT * FROM installments " +
        "WHERE monthKey = :monthKey LIMIT 1"
    )
    fun observeInstallment(
        monthKey: String
    ): Flow<InstallmentEntity?>

    @Query(
        "SELECT * FROM installments " +
        "WHERE monthKey = :monthKey LIMIT 1"
    )
    suspend fun getInstallment(monthKey: String): InstallmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallment(installment: InstallmentEntity)

    // =========================
    // PENGATURAN
    // =========================

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun observeSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: AppSettingsEntity)

    // =========================
    // LAPORAN BULANAN
    // =========================

    @Query("SELECT * FROM monthly_reports ORDER BY monthKey DESC")
    fun observeReports(): Flow<List<MonthlyReportEntity>>

    @Query(
        "SELECT * FROM monthly_reports " +
        "WHERE monthKey = :monthKey LIMIT 1"
    )
    suspend fun getReport(monthKey: String): MonthlyReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: MonthlyReportEntity)
}
