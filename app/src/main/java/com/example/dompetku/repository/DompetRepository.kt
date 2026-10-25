package com.example.dompetku.repository

import com.example.dompetku.data.AppSettingsEntity
import com.example.dompetku.data.BudgetEntity
import com.example.dompetku.data.DompetDao
import com.example.dompetku.data.InstallmentEntity
import com.example.dompetku.data.MonthlyReportEntity
import com.example.dompetku.data.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DompetRepository(
    private val dao: DompetDao
) {
    private fun currentMonthKey(): String {
        return SimpleDateFormat(
            "yyyy-MM",
            Locale.US
        ).format(Date())
    }

    fun observeBudgets(
        monthKey: String = currentMonthKey()
    ): Flow<List<BudgetEntity>> {
        return dao.observeBudgets(monthKey)
    }

    suspend fun saveBudget(budget: BudgetEntity) {
        dao.saveBudget(budget)
    }

    suspend fun saveBudgets(budgets: List<BudgetEntity>) {
        dao.saveBudgets(budgets)
    }

    fun observeTransactions(
        monthKey: String = currentMonthKey()
    ): Flow<List<TransactionEntity>> {
        return dao.observeTransactions(monthKey)
    }

    fun observeIncomeTotal(
        monthKey: String = currentMonthKey()
    ): Flow<Long> {
        return dao.observeIncomeTotal(monthKey)
    }

    fun observeExpenseTotal(
        monthKey: String = currentMonthKey()
    ): Flow<Long> {
        return dao.observeExpenseTotal(monthKey)
    }

    suspend fun addTransaction(
        category: String,
        amount: Long,
        type: String,
        note: String = ""
    ): Long {
        require(amount > 0) {
            "Nominal transaksi harus lebih dari nol."
        }

        require(type in listOf("INCOME", "EXPENSE", "TRANSFER")) {
            "Jenis transaksi tidak valid."
        }

        return dao.insertTransaction(
            TransactionEntity(
                category = category,
                amount = amount,
                type = type,
                note = note,
                monthKey = currentMonthKey()
            )
        )
    }

    fun observeInstallment(
        monthKey: String = currentMonthKey()
    ): Flow<InstallmentEntity?> {
        return dao.observeInstallment(monthKey)
    }

    suspend fun saveInstallment(installment: InstallmentEntity) {
        dao.saveInstallment(installment)
    }

    fun observeReports(): Flow<List<MonthlyReportEntity>> {
        return dao.observeReports()
    }

    suspend fun getReport(monthKey: String): MonthlyReportEntity? {
        return dao.getReport(monthKey)
    }

    suspend fun saveReport(report: MonthlyReportEntity) {
        dao.saveReport(report)
    }

    fun observeSettings(): Flow<AppSettingsEntity?> {
        return dao.observeSettings()
    }

    suspend fun saveSettings(settings: AppSettingsEntity) {
        dao.saveSettings(settings)
    }

    suspend fun getSettings(): AppSettingsEntity? {
        return dao.getSettings()
    }

    suspend fun getBudgetsForMonth(
        monthKey: String
    ): List<BudgetEntity> {
        return dao.getBudgetsForMonth(monthKey)
    }

    suspend fun getTransactionsForMonth(
        monthKey: String
    ): List<TransactionEntity> {
        return dao.getTransactionsForMonth(monthKey)
    }

    suspend fun getInstallmentForMonth(
        monthKey: String
    ): InstallmentEntity? {
        return dao.getInstallmentForMonth(monthKey)
    }
}

