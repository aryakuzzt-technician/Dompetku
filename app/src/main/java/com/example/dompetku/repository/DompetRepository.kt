package com.example.dompetku.data

import kotlinx.coroutines.flow.Flow

class DompetRepository(
    private val dao: DompetDao
) {
    fun observeBudgets(monthKey: String): Flow<List<BudgetEntity>> =
        dao.observeBudgets(monthKey)

    fun observeTransactions(monthKey: String): Flow<List<TransactionEntity>> =
        dao.observeTransactions(monthKey)

    fun observeIncome(monthKey: String): Flow<Long> =
        dao.observeIncome(monthKey)

    fun observeExpenses(monthKey: String): Flow<Long> =
        dao.observeExpenses(monthKey)

    fun observeInstallment(monthKey: String): Flow<InstallmentEntity?> =
        dao.observeInstallment(monthKey)

    fun observeSettings(): Flow<AppSettingsEntity?> =
        dao.observeSettings()

    fun observeReports(): Flow<List<MonthlyReportEntity>> =
        dao.observeReports()

    suspend fun getBudgets(monthKey: String): List<BudgetEntity> =
        dao.getBudgets(monthKey)

    suspend fun getTransactions(monthKey: String): List<TransactionEntity> =
        dao.getTransactions(monthKey)

    suspend fun getInstallment(monthKey: String): InstallmentEntity? =
        dao.getInstallment(monthKey)

    suspend fun getSettings(): AppSettingsEntity? =
        dao.getSettings()

    suspend fun getReport(monthKey: String): MonthlyReportEntity? =
        dao.getReport(monthKey)

    suspend fun saveBudget(budget: BudgetEntity) {
        dao.insertBudget(budget)
    }

    suspend fun saveBudgets(budgets: List<BudgetEntity>) {
        budgets.forEach { dao.insertBudget(it) }
    }

    suspend fun addTransaction(
        category: String,
        amount: Long,
        type: String,
        note: String = ""
    ) {
        require(amount > 0L) { "Nominal transaksi harus lebih dari nol." }
        require(type in listOf("INCOME", "EXPENSE", "TRANSFER")) {
            "Jenis transaksi tidak valid."
        }

        val monthKey = java.text.SimpleDateFormat(
            "yyyy-MM",
            java.util.Locale.US
        ).format(java.util.Date())

        dao.insertTransaction(
            TransactionEntity(
                category = category,
                amount = amount,
                type = type,
                note = note,
                monthKey = monthKey
            )
        )
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

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }
}
