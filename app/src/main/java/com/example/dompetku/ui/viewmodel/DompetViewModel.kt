
package com.example.dompetku.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dompetku.data.AppSettingsEntity
import com.example.dompetku.data.BudgetEntity
import com.example.dompetku.data.DompetDatabase
import com.example.dompetku.data.DompetRepository
import com.example.dompetku.data.InstallmentEntity
import com.example.dompetku.data.MonthlyReportEntity
import com.example.dompetku.data.TransactionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DompetUiState(
    val salary: Long = 2_500_000L,
    val income: Long = 0L,
    val expenses: Long = 0L,
    val mandatorySavings: Long = 0L,
    val remainingBalance: Long = 0L,
    val budgets: List<BudgetEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val installmentPaid: Boolean = false,
    val message: String = ""
)

class DompetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DompetRepository(
        DompetDatabase.getDatabase(application).dompetDao()
    )

    private fun currentMonthKey(): String =
        SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

    private val _uiState = MutableStateFlow(DompetUiState())
    val uiState: StateFlow<DompetUiState> = _uiState.asStateFlow()

    init {
        observeData()
        initializeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.observeBudgets(),
                repository.observeTransactions(),
                repository.observeIncome(),
                repository.observeExpenses(),
                repository.observeSettings(),
                repository.observeInstallment()
            ) { values ->
                val budgets = values[0] as List<BudgetEntity>
                val transactions = values[1] as List<TransactionEntity>
                val income = values[2] as Long
                val expenses = values[3] as Long
                val settings = values[4] as AppSettingsEntity?
                val installment = values[5] as InstallmentEntity?

                val salary = settings?.monthlySalary ?: 2_500_000L

                val savings = transactions
                    .filter {
                        it.type == "EXPENSE" &&
                            it.category == "Tabungan wajib"
                    }
                    .sumOf { it.amount }

                DompetUiState(
                    salary = salary,
                    income = income,
                    expenses = expenses,
                    mandatorySavings = savings +
                        (settings?.accumulatedMandatorySavings ?: 0L),
                    remainingBalance = salary + income - expenses,
                    budgets = budgets,
                    transactions = transactions,
                    installmentPaid = installment?.isPaid ?: false,
                    message = _uiState.value.message
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun initializeData() {
        viewModelScope.launch {
            val month = currentMonthKey()

            if (repository.getSettings() == null) {
                repository.saveSettings(AppSettingsEntity())
            }

            if (repository.getInstallment(month) == null) {
                val settings = repository.getSettings() ?: AppSettingsEntity()

                repository.saveInstallment(
                    InstallmentEntity(
                        amount = settings.installmentAmount,
                        monthKey = month
                    )
                )
            }

            if (repository.getBudgets(month).isEmpty()) {
                val settings = repository.getSettings() ?: AppSettingsEntity()

                repository.saveBudgets(
                    listOf(
                        BudgetEntity(
                            category = "Cicilan wajib",
                            amount = settings.installmentAmount,
                            monthKey = month
                        ),
                        BudgetEntity(
                            category = "Uang bensin",
                            amount = settings.monthlySalary *
                                settings.fuelPercentage.toLong() / 100,
                            percentage = settings.fuelPercentage,
                            monthKey = month
                        ),
                        BudgetEntity(
                            category = "Tabungan wajib",
                            amount = settings.monthlySalary *
                                settings.savingsPercentage.toLong() / 100,
                            percentage = settings.savingsPercentage,
                            monthKey = month
                        ),
                        BudgetEntity(
                            category = "Dana darurat",
                            amount = settings.monthlySalary *
                                settings.emergencyPercentage.toLong() / 100,
                            percentage = settings.emergencyPercentage,
                            monthKey = month
                        )
                    )
                )
            }
        }
    }

    private fun setMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message)
    }

    private suspend fun isMonthOpen(): Boolean {
        val report = repository.getReport(currentMonthKey())

        if (report?.isClosed == true) {
            setMessage("Bulan ini sudah ditutup.")
            return false
        }

        return true
    }

    fun saveSalary(amount: Long) {
        if (amount <= 0L) {
            setMessage("Gaji harus lebih dari nol.")
            return
        }

        viewModelScope.launch {
            if (!isMonthOpen()) return@launch

            val settings = repository.getSettings() ?: AppSettingsEntity()
            repository.saveSettings(settings.copy(monthlySalary = amount))
            setMessage("Gaji berhasil disimpan.")
        }
    }

    fun saveSettings(settings: AppSettingsEntity) {
        if (settings.monthlySalary <= 0L || settings.installmentAmount < 0L) {
            setMessage("Nominal gaji atau cicilan tidak valid.")
            return
        }

        viewModelScope.launch {
            if (!isMonthOpen()) return@launch

            val month = currentMonthKey()
            repository.saveSettings(settings)

            repository.saveBudgets(
                listOf(
                    BudgetEntity(
                        category = "Cicilan wajib",
                        amount = settings.installmentAmount,
                        monthKey = month
                    ),
                    BudgetEntity(
                        category = "Uang bensin",
                        amount = settings.monthlySalary *
                            settings.fuelPercentage.toLong() / 100,
                        percentage = settings.fuelPercentage,
                        monthKey = month
                    ),
                    BudgetEntity(
                        category = "Tabungan wajib",
                        amount = settings.monthlySalary *
                            settings.savingsPercentage.toLong() / 100,
                        percentage = settings.savingsPercentage,
                        monthKey = month
                    ),
                    BudgetEntity(
                        category = "Dana darurat",
                        amount = settings.monthlySalary *
                            settings.emergencyPercentage.toLong() / 100,
                        percentage = settings.emergencyPercentage,
                        monthKey = month
                    )
                )
            )

            val installment = repository.getInstallment(month)
            if (installment != null && !installment.isPaid) {
                repository.saveInstallment(
                    installment.copy(amount = settings.installmentAmount)
                )
            }

            setMessage("Pengaturan berhasil disimpan.")
        }
    }

    fun addIncome(
        category: String,
        amount: Long,
        note: String = ""
    ) {
        addTransaction(category, amount, "INCOME", note)
    }

    fun addExpense(
        category: String,
        amount: Long,
        note: String = ""
    ) {
        addTransaction(category, amount, "EXPENSE", note)
    }

    private fun addTransaction(
        category: String,
        amount: Long,
        type: String,
        note: String
    ) {
        if (category.isBlank() || amount <= 0L) {
            setMessage("Kategori dan nominal harus valid.")
            return
        }

        viewModelScope.launch {
            if (!isMonthOpen()) return@launch

            repository.addTransaction(
                category = category.trim(),
                amount = amount,
                type = type,
                note = note
            )

            setMessage("Transaksi berhasil disimpan.")
        }
    }

    fun saveBudget(
        category: String,
        amount: Long,
        percentage: Float = 0f
    ) {
        if (category.isBlank() || amount < 0L) {
            setMessage("Kategori atau nominal anggaran tidak valid.")
            return
        }

        viewModelScope.launch {
            if (!isMonthOpen()) return@launch

            repository.saveBudget(
                BudgetEntity(
                    category = category.trim(),
                    amount = amount,
                    percentage = percentage,
                    monthKey = currentMonthKey()
                )
            )

            setMessage("Anggaran berhasil disimpan.")
        }
    }

    fun markInstallmentPaid() {
        viewModelScope.launch {
            if (!isMonthOpen()) return@launch

            val month = currentMonthKey()
            val installment = repository.getInstallment(month)
                ?: InstallmentEntity(monthKey = month)

            if (installment.isPaid) {
                setMessage("Cicilan bulan ini sudah ditandai lunas.")
                return@launch
            }

            if (installment.amount <= 0L) {
                repository.saveInstallment(installment.copy(isPaid = true))
                setMessage("Cicilan ditandai lunas.")
                return@launch
            }

            repository.addTransaction(
                category = "Cicilan wajib",
                amount = installment.amount,
                type = "EXPENSE",
                note = "Pembayaran cicilan bulan $month"
            )

            repository.saveInstallment(installment.copy(isPaid = true))
            setMessage("Cicilan berhasil ditandai lunas.")
        }
    }

    fun closeMonth() {
        viewModelScope.launch {
            val month = currentMonthKey()

            if (repository.getReport(month)?.isClosed == true) {
                setMessage("Laporan bulan ini sudah ditutup.")
                return@launch
            }

            val transactions = repository.getTransactions(month)
            val settings = repository.getSettings() ?: AppSettingsEntity()

            val income = transactions
                .filter { it.type == "INCOME" }
                .sumOf { it.amount }

            val expenses = transactions
                .filter { it.type == "EXPENSE" }
                .sumOf { it.amount }

            val savings = transactions
                .filter {
                    it.type == "EXPENSE" &&
                        it.category == "Tabungan wajib"
                }
                .sumOf { it.amount }

            val remaining = settings.monthlySalary + income - expenses

            repository.saveReport(
                MonthlyReportEntity(
                    monthKey = month,
                    totalIncome = income,
                    totalExpense = expenses,
                    totalMandatorySavings = savings,
                    remainingBalance = remaining,
                    isClosed = true
                )
            )

            repository.saveSettings(
                settings.copy(
                    accumulatedMandatorySavings =
                        settings.accumulatedMandatorySavings + savings,
                    accumulatedRemainingBalance =
                        settings.accumulatedRemainingBalance + remaining
                )
            )

            setMessage("Laporan bulan $month berhasil ditutup.")
        }
    }
}
