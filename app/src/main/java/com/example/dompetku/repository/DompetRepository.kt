
package com.example.dompetku.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DompetRepository(
    private val dao: DompetDao
) {

    private fun currentMonthKey(): String {
        return SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
    }

    // ==================== OBSERVE DATA ====================

    fun observeBudgets(monthKey: String = currentMonthKey()): Flow<List<BudgetEntity>> {
        return dao.observeBudgets(monthKey)
    }

    fun observeTransactions(monthKey: String = currentMonthKey()): Flow<List<TransactionEntity>> {
        return dao.observeTransactions(monthKey)
    }

    fun observeIncome(monthKey: String = currentMonthKey()): Flow<Long> {
        return dao.observeIncome(monthKey)
    }

    fun observeExpenses(monthKey: String = currentMonthKey()): Flow<Long> {
        return dao.observeExpenses(monthKey)
    }

    fun observeInstallment(monthKey: String = currentMonthKey()): Flow<InstallmentEntity?> {
        return dao.observeInstallment(monthKey)
    }

    fun observeSettings(): Flow<AppSettingsEntity?> {
        return dao.observeSettings()
    }

    fun observeReports(): Flow<List<MonthlyReportEntity>> {
        return dao.observeReports()
    }

    // ==================== GET DATA ====================

    suspend fun getBudgets(monthKey: String = currentMonthKey()): List<BudgetEntity> {
        return dao.getBudgets(monthKey)
    }

    suspend fun getTransactions(monthKey: String = currentMonthKey()): List<TransactionEntity> {
        return dao.getTransactions(monthKey)
    }

    suspend fun getInstallment(monthKey: String = currentMonthKey()): InstallmentEntity? {
        return dao.getInstallment(monthKey)
    }

    suspend fun getSettings(): AppSettingsEntity? {
        return dao.getSettings()
    }

    suspend fun getReport(monthKey: String = currentMonthKey()): MonthlyReportEntity? {
        return dao.getReport(monthKey)
    }

    // ==================== SAVE DATA ====================

    suspend fun saveBudget(budget: BudgetEntity) {
        dao.insertBudget(budget)
    }

    suspend fun saveBudgets(budgets: List<BudgetEntity>) {
        dao.insertBudgets(budgets)
    }

    suspend fun saveInstallment(installment: InstallmentEntity) {
        dao.insertInstallment(installment)
    }

    suspend fun saveSettings(settings: AppSettingsEntity) {
        dao.insertSettings(settings)
    }

    suspend fun saveReport(report: MonthlyReportEntity) {
        dao.insertReport(report)
    }

    // ==================== TRANSACTIONS ====================

    suspend fun addTransaction(
        category: String,
        amount: Long,
        type: String,
        note: String = ""
    ) {
        require(amount > 0L) {
            "Nominal transaksi harus lebih dari nol."
        }

        require(type in listOf("INCOME", "EXPENSE", "TRANSFER")) {
            "Jenis transaksi tidak valid."
        }

        dao.insertTransaction(
            TransactionEntity(
                category = category,
                amount = amount,
                type = type,
                note = note.trim(),
                monthKey = currentMonthKey()
            )
        )
    }

    suspend fun deleteTransaction(transactionId: Long) {
        dao.deleteTransaction(transactionId)
    }

    suspend fun deleteBudget(
        category: String,
        monthKey: String = currentMonthKey()
    ) {
        dao.deleteBudget(monthKey, category)
    }
}
