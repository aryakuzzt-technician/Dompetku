
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

class DompetViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository = DompetRepository(
        DompetDatabase.getDatabase(application).dompetDao()
    )

    private fun currentMonthKey(): String =
        SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

    private val _uiState = MutableStateFlow(DompetUiState())
    val uiState: StateFlow<DompetUiState> = _uiState.asStateFlow()

    init {
        observeData()
        initializeSettings()
    }

    private fun observeData() {
        val month = currentMonthKey()

        viewModelScope.launch {
            repository.observeIncome(month).collect { value ->
                _uiState.value = _uiState.value.copy(income = value)
                recalculate()
            }
        }

        viewModelScope.launch {
            repository.observeExpenses(month).collect { value ->
                _uiState.value = _uiState.value.copy(expenses = value)
                recalculate()
            }
        }

        viewModelScope.launch {
            repository.observeBudgets(month).collect { value ->
                _uiState.value = _uiState.value.copy(budgets = value)
            }
        }

        viewModelScope.launch {
            repository.observeTransactions(month).collect { value ->
                _uiState.value = _uiState.value.copy(transactions = value)
            }
        }

        viewModelScope.launch {
            repository.observeInstallment(month).collect { installment ->
                _uiState.value = _uiState.value.copy(
                    installmentPaid = installment?.isPaid ?: false
                )
            }
        }

        viewModelScope.launch {
            repository.observeSettings().collect { settings ->
                if (settings != null) {
                    _uiState.value = _uiState.value.copy(
                        salary = settings.monthlySalary,
                        mandatorySavings =
                            settings.accumulatedMandatorySavings
                    )
                    recalculate()
                }
            }
        }
    }

    private fun recalculate() {
        val state = _uiState.value
        _uiState.value = state.copy(
            remainingBalance = state.salary + state.income - state.expenses
        )
    }

    private fun setMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message)
    }

    private suspend fun ensureMonthOpen(): Boolean {
        val report = repository.getReport(currentMonthKey())
        if (report?.isClosed == true) {
            setMessage("Bulan ini sudah ditutup.")
            return false
        }
        return true
    }

    private fun initializeSettings() {
        viewModelScope.launch {
            if (repository.getSettings() == null) {
                repository.saveSettings(AppSettingsEntity())
            }

            val month = currentMonthKey()

            if (repository.getInstallment(month) == null) {
                repository.saveInstallment(
                    InstallmentEntity(monthKey = month)
                )
            }

            if (repository.getBudgets(month).isEmpty()) {
                repository.saveBudgets(
                    listOf(
                        BudgetEntity("Cicilan wajib", 810_000L, 0f, month),
                        BudgetEntity("Uang bensin", 250_000L, 10f, month),
                        BudgetEntity("Tabungan wajib", 250_000L, 10f, month),
                        BudgetEntity("Dana darurat", 100_000L, 4f, month)
                    )
                )
            }
        }
    }

    fun saveSalary(amount: Long) {
        if (amount <= 0L) {
            setMessage("Gaji harus lebih dari nol.")
            return
        }

        viewModelScope.launch {
            val settings = repository.getSettings() ?: AppSettingsEntity()
            repository.saveSettings(settings.copy(monthlySalary = amount))
            setMessage("Gaji berhasil diperbarui.")
        }
    }

    fun saveSettings(settings: AppSettingsEntity) {
        if (settings.monthlySalary <= 0L || settings.installmentAmount < 0L) {
            setMessage("Gaji harus lebih dari nol dan cicilan tidak boleh negatif.")
            return
        }

        viewModelScope.launch {
            if (!ensureMonthOpen()) return@launch

            repository.saveSettings(settings)

            val month = currentMonthKey()
            val budgets = listOf(
                BudgetEntity(
                    "Cicilan wajib",
                    settings.installmentAmount,
                    0f,
                    month
                ),
                BudgetEntity(
                    "Uang bensin",
                    (settings.monthlySalary * settings.fuelPercentage / 100).toLong(),
                    settings.fuelPercentage,
                    month
                ),
                BudgetEntity(
                    "Tabungan wajib",
                    (settings.monthlySalary * settings.savingsPercentage / 100).toLong(),
                    settings.savingsPercentage,
                    month
                ),
                BudgetEntity(
                    "Dana darurat",
                    (settings.monthlySalary * settings.emergencyPercentage / 100).toLong(),
                    settings.emergencyPercentage,
                    month
                )
            )

            repository.saveBudgets(budgets)

            val installment = repository.getInstallment(month)
                ?: InstallmentEntity(monthKey = month)

            repository.saveInstallment(
                installment.copy(amount = settings.installmentAmount)
            )

            setMessage("Pengaturan berhasil disimpan.")
        }
    }

    fun addIncome(category: String, amount: Long, note: String = "") {
        addTransaction(category, amount, "INCOME", note)
    }

    fun addExpense(category: String, amount: Long, note: String = "") {
        addTransaction(category, amount, "EXPENSE", note)
    }

    private fun addTransaction(
        category: String,
        amount: Long,
        type: String,
        note: String
    ) {
        if (amount <= 0L) {
            setMessage("Nominal harus lebih dari nol.")
            return
        }

        viewModelScope.launch {
            if (!ensureMonthOpen()) return@launch

            repository.addTransaction(category, amount, type, note)
            setMessage("Transaksi berhasil disimpan.")
        }
    }

    fun saveBudget(category: String, amount: Long, percentage: Float) {
        if (amount < 0L) {
            setMessage("Anggaran tidak boleh negatif.")
            return
        }

        viewModelScope.launch {
            if (!ensureMonthOpen()) return@launch

            repository.saveBudget(
                BudgetEntity(
                    category = category,
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
            if (!ensureMonthOpen()) return@launch

            val month = currentMonthKey()
            val installment = repository.getInstallment(month)
                ?: InstallmentEntity(monthKey = month)

            if (installment.isPaid) {
                setMessage("Cicilan bulan ini sudah ditandai lunas.")
                return@launch
            }

            repository.addTransaction(
                "Cicilan wajib",
                installment.amount,
                "EXPENSE",
                "Pembayaran cicilan wajib"
            )

            repository.saveInstallment(installment.copy(isPaid = true))
            setMessage("Cicilan ditandai lunas.")
        }
    }

    fun closeMonth() {
        viewModelScope.launch {
            val month = currentMonthKey()

            if (repository.getReport(month)?.isClosed == true) {
                setMessage("Bulan ini sudah pernah ditutup.")
                return@launch
            }

            val settings = repository.getSettings() ?: AppSettingsEntity()
            val transactions = repository.getTransactions(month)

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

            setMessage("Laporan $month berhasil ditutup.")
        }
    }
    }
    
