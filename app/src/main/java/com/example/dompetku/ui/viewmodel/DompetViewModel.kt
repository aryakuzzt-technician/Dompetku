package com.example.dompetku.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dompetku.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DompetViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DompetDatabase.getDatabase(application).dompetDao()

    val activePeriod: StateFlow<ActivePeriodEntity?> = dao.getActivePeriod()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentTransactions: StateFlow<List<TransactionEntity>> = activePeriod.flatMapLatest { period ->
        if (period != null) dao.getTransactionsByMonth(period.monthYear) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<MonthlyReportEntity>> = dao.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            if (dao.getActivePeriodDirect() == null) {
                initDefaultPeriod(2600000.0, "Oktober 2026")
            }
        }
    }

    fun calculateAllocation(salary: Double): Map<String, Double> {
        val cicilan = 810000.0
        val sisaBersih = (salary - cicilan).coerceAtLeast(0.0)
        return mapOf(
            "CICILAN" to cicilan,
            "BENSIN" to sisaBersih * 0.15,
            "TABUNGAN" to sisaBersih * 0.30,
            "DARURAT" to sisaBersih * 0.15,
            "SISA" to sisaBersih * 0.40
        )
    }

    fun initDefaultPeriod(salary: Double, monthYear: String) {
        viewModelScope.launch {
            val alloc = calculateAllocation(salary)
            val period = ActivePeriodEntity(
                id = 1,
                monthYear = monthYear,
                salary = salary,
                cicilanStatus = false,
                cicilanAmount = alloc["CICILAN"]!!,
                bensinBudget = alloc["BENSIN"]!!,
                bensinBalance = alloc["BENSIN"]!!,
                tabunganBudget = alloc["TABUNGAN"]!!,
                tabunganBalance = alloc["TABUNGAN"]!!,
                daruratBudget = alloc["DARURAT"]!!,
                daruratBalance = alloc["DARURAT"]!!,
                sisaBudget = alloc["SISA"]!!,
                sisaBalance = alloc["SISA"]!!,
                totalAccumulatedTabungan = 0.0,
                totalAccumulatedSisa = 0.0
            )
            dao.insertOrUpdateActivePeriod(period)
        }
    }

    fun updateSalary(newSalary: Double) {
        viewModelScope.launch {
            val current = dao.getActivePeriodDirect() ?: return@launch
            if (newSalary < 810000.0) return@launch // Validasi gaji minimal

            val alloc = calculateAllocation(newSalary)
            val updated = current.copy(
                salary = newSalary,
                bensinBudget = alloc["BENSIN"]!!,
                bensinBalance = alloc["BENSIN"]!!,
                tabunganBudget = alloc["TABUNGAN"]!!,
                tabunganBalance = alloc["TABUNGAN"]!!,
                daruratBudget = alloc["DARURAT"]!!,
                daruratBalance = alloc["DARURAT"]!!,
                sisaBudget = alloc["SISA"]!!,
                sisaBalance = alloc["SISA"]!!
            )
            dao.insertOrUpdateActivePeriod(updated)
        }
    }

    fun payCicilan() {
        viewModelScope.launch {
            val current = dao.getActivePeriodDirect() ?: return@launch
            if (current.cicilanStatus) return@launch

            val updated = current.copy(cicilanStatus = true)
            dao.insertOrUpdateActivePeriod(updated)
            dao.insertTransaction(
                TransactionEntity(
                    monthYear = current.monthYear,
                    category = "CICILAN",
                    type = "PENGELUARAN",
                    amount = current.cicilanAmount,
                    note = "Pembayaran Cicilan Wajib Bulan Ini"
                )
            )
        }
    }

    fun addTransaction(category: String, type: String, amount: Double, note: String) {
        viewModelScope.launch {
            val current = dao.getActivePeriodDirect() ?: return@launch
            var bB = current.bensinBalance
            var tB = current.tabunganBalance
            var dB = current.daruratBalance
            var sB = current.sisaBalance
            var accTabungan = current.totalAccumulatedTabungan

            val delta = if (type == "PEMASUKAN") amount else -amount

            when (category) {
                "BENSIN" -> bB += delta
                "TABUNGAN" -> {
                    tB += delta
                    if (type == "PEMASUKAN") accTabungan += amount
                }
                "DARURAT" -> dB += delta
                "SISA" -> sB += delta
            }

            val updated = current.copy(
                bensinBalance = bB,
                tabunganBalance = tB,
                daruratBalance = dB,
                sisaBalance = sB,
                totalAccumulatedTabungan = accTabungan
            )
            dao.insertOrUpdateActivePeriod(updated)
            dao.insertTransaction(
                TransactionEntity(
                    monthYear = current.monthYear,
                    category = category,
                    type = type,
                    amount = amount,
                    note = note
                )
            )
        }
    }

    fun transferBalance(source: String, target: String, amount: Double) {
        viewModelScope.launch {
            val current = dao.getActivePeriodDirect() ?: return@launch
            if (amount <= 0) return@launch

            val sourceBal = when(source) {
                "BENSIN" -> current.bensinBalance
                "TABUNGAN" -> current.tabunganBalance
                "DARURAT" -> current.daruratBalance
                "SISA" -> current.sisaBalance
                else -> 0.0
            }

            if (amount > sourceBal) return@launch // Cegah saldo minus

            dao.executeTransfer(source, target, amount, current.monthYear, current)
        }
    }

    fun closeMonth(nextMonthYear: String) {
        viewModelScope.launch {
            val current = dao.getActivePeriodDirect() ?: return@launch

            val totalSavedThisMonth = current.tabunganBalance.coerceAtLeast(0.0)
            val sisaThisMonth = current.sisaBalance.coerceAtLeast(0.0)
            val finalBal = current.bensinBalance + current.tabunganBalance + current.daruratBalance + current.sisaBalance

            val report = MonthlyReportEntity(
                monthYear = current.monthYear,
                salary = current.salary,
                totalIncome = current.salary,
                totalExpense = if (current.cicilanStatus) current.cicilanAmount else 0.0,
                totalSaved = totalSavedThisMonth,
                finalBalance = finalBal
            )
            dao.insertReport(report)

            val newAccTabungan = current.totalAccumulatedTabungan + totalSavedThisMonth
            val newAccSisa = current.totalAccumulatedSisa + sisaThisMonth

            val alloc = calculateAllocation(current.salary)
            val newPeriod = ActivePeriodEntity(
                id = 1,
                monthYear = nextMonthYear,
                salary = current.salary,
                cicilanStatus = false,
                cicilanAmount = alloc["CICILAN"]!!,
                bensinBudget = alloc["BENSIN"]!!,
                bensinBalance = alloc["BENSIN"]!!,
                tabunganBudget = alloc["TABUNGAN"]!!,
                tabunganBalance = alloc["TABUNGAN"]!!,
                daruratBudget = alloc["DARURAT"]!!,
                daruratBalance = alloc["DARURAT"]!!,
                sisaBudget = alloc["SISA"]!!,
                sisaBalance = alloc["SISA"]!!,
                totalAccumulatedTabungan = newAccTabungan,
                totalAccumulatedSisa = newAccSisa
            )

            dao.insertOrUpdateActivePeriod(newPeriod)
        }
    }
}

