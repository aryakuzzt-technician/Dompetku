package com.example.dompetku.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dompetku.data.AppSettingsEntity
import com.example.dompetku.data.BudgetEntity
import com.example.dompetku.data.DompetDatabase
import com.example.dompetku.data.InstallmentEntity
import com.example.dompetku.data.MonthlyReportEntity
import com.example.dompetku.repository.DompetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    val installmentPaid: Boolean = false,
    val message: String = ""
)

class DompetViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao = DompetDatabase
        .getDatabase(application)
        .dompetDao()

    private val repository = DompetRepository(dao)

    private fun currentMonthKey(): String {
        return SimpleDateFormat(
            "yyyy-MM",
            Locale.US
        ).format(Date())
    }

    private val _uiState = MutableStateFlow(DompetUiState())
    val uiState: StateFlow<DompetUiState> = _uiState.asStateFlow()

    init {
        observeData()
        initializeSettings()
    }

    private fun observeData() {
        val month = currentMonthKey()

        viewModelScope.launch {
            repository.observeIncomeTotal(month).collect { income ->
                _uiState.value = _uiState.value.copy(income = income)
                recalculate()
            }
        }

        viewModelScope.launch {
            repository.observeExpenseTotal(month).collect { expenses ->
                _uiState.value = _uiState.value.copy(expenses = expenses)
                recalculate()
            }
        }

        viewModelScope.launch {
            repository.observeBudgets(month).collect { budgets ->
                _uiState.value = _uiState.value.copy(budgets = budgets)
                recalculate()
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
                        mandatorySavings = settings.accumulatedMandatorySavings
                    )
                    recalculate()
                }
            }
        }
    }

    private fun recalculate() {
        val state = _uiState.value
        val balance = state.salary + state.income - state.expenses

        _uiState.value = state.copy(
            remainingBalance = balance
        )
    }

    private fun initializeSettings() {
        viewModelScope.launch {
            if (repository.getSettings() == null) {
                repository.saveSettings(AppSettingsEntity())
            }

            val month = currentMonthKey()

            if (repository.getInstallmentForMonth(month) == null) {
                repository.saveInstallment(
                    InstallmentEntity(monthKey = month)
                )
            }

            if (repository.getBudgetsForMonth(month).isEmpty()) {
                repository.saveBudgets(
                    listOf(
                        BudgetEntity(
                            category = "Cicilan wajib",
                            amount = 810_000L,
                            monthKey = month
                        ),
                        BudgetEntity(
                            category = "Uang bensin",
                            amount = 250_000L,
                            percentage = 10f,
                            monthKey = month
                        ),
                        BudgetEntity(
                            category = "Tabungan wajib",
                            amount = 250_000L,
                            percentage = 10f,
                            monthKey = month
                        ),
                        BudgetEntity(
                            category = "Dana darurat",
                            amount = 100_000L,
                            percentage = 4f,
                            monthKey = month
                        )
                    )
                )
            }
        }
    }

    fun saveSalary(amount: Long) {
        if (amount < 0) return

        viewModelScope.launch {
            val current = repository.getSettings() ?: AppSettingsEntity()
            repository.saveSettings(
                current.copy(monthlySalary = amount)
            )
            _uiState.value = _uiState.value.copy(
                message = "Gaji berhasil disimpan."
            )
        }
    }

    fun addIncome(
        category: String,
        amount: Long,
        note: String = ""
    ) {
        viewModelScope.launch {
            runCatching {
                repository.addTransaction(
                    category = category,
                    amount = amount,
                    type = "INCOME",
                    note = note
                )
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    message = "Pemasukan berhasil dicatat."
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    message = it.message ?: "Gagal menyimpan pemasukan."
                )
            }
        }
    }

    fun addExpense(
        category: String,
        amount: Long,
        note: String = ""
    ) {
        viewModelScope.launch {
            runCatching {
                repository.addTransaction(
                    category = category,
                    amount = amount,
                    type = "EXPENSE",
                    note = note
                )
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    message = "Pengeluaran berhasil dicatat."
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    message = it.message ?: "Gagal menyimpan pengeluaran."
                )
            }
        }
    }

    fun markInstallmentPaid() {
        viewModelScope.launch {
            val month = currentMonthKey()
            val current = repository.getInstallmentForMonth(month)
                ?: InstallmentEntity(monthKey = month)

            if (current.isPaid) {
                _uiState.value = _uiState.value.copy(
                    message = "Cicilan bulan ini sudah ditandai lunas."
                )
                return@launch
            }

            repository.saveInstallment(
                current.copy(isPaid = true)
            )

            repository.addTransaction(
                category = "Cicilan wajib",
                amount = current.amount,
                type = "EXPENSE",
                note = "Pembayaran cicilan wajib"
            )

            _uiState.value = _uiState.value.copy(
                message = "Pembayaran cicilan berhasil dicatat."
            )
        }
    }

    fun saveBudget(
        category: String,
        amount: Long,
        percentage: Float = 0f
    ) {
        if (amount < 0) return

        viewModelScope.launch {
            repository.saveBudget(
                BudgetEntity(
                    category = category,
                    amount = amount,
                    percentage = percentage,
                    monthKey = currentMonthKey()
                )
            )
            _uiState.value = _uiState.value.copy(
                message = "Anggaran berhasil disimpan."
            )
        }
    }

    fun closeMonth() {
        viewModelScope.launch {
            val month = currentMonthKey()

            if (repository.getReport(month) != null) {
                _uiState.value = _uiState.value.copy(
                    message = "Laporan bulan ini sudah ditutup."
                )
                return@launch
            }

            val income = repository.observeIncomeTotal(month).first()
            val expenses = repository.observeExpenseTotal(month).first()
            val settings = repository.getSettings() ?: AppSettingsEntity()
            val transactions = repository.getTransactionsForMonth(month)

            val savings = transactions
                .filter {
                    it.category == "Tabungan wajib" &&
                    it.type == "EXPENSE"
                }
                .sumOf { it.amount }

            val remaining = settings.monthlySalary + income - expenses

            repository.saveReport(
                MonthlyReportEntity(
                    monthKey = month,
                    totalIncome = income,
                    totalExpense = expenses,
                    totalMandatorySavings = savings,
                    remainingBalance = remaining
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

            _uiState.value = _uiState.value.copy(
                message = "Laporan bulan $month berhasil ditutup."
            )
        }
    }
}
