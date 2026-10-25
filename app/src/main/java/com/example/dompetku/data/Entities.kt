package com.example.dompetku.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_period")
data class ActivePeriodEntity(
    @PrimaryKey val id: Int = 1,
    val monthYear: String, // e.g., "Oktober 2026"
    val salary: Double,
    val cicilanStatus: Boolean = false, // true jika sudah dibayar
    val cicilanAmount: Double = 810000.0,
    val bensinBudget: Double,
    val bensinBalance: Double,
    val tabunganBudget: Double,
    val tabunganBalance: Double,
    val daruratBudget: Double,
    val daruratBalance: Double,
    val sisaBudget: Double,
    val sisaBalance: Double,
    val totalAccumulatedTabungan: Double = 0.0,
    val totalAccumulatedSisa: Double = 0.0
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthYear: String,
    val category: String, // CICILAN, BENSIN, TABUNGAN, DARURAT, SISA
    val type: String,     // PEMASUKAN, PENGELUARAN, TRANSFER
    val amount: Double,
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "monthly_reports")
data class MonthlyReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthYear: String,
    val salary: Double,
    val totalIncome: Double,
    val totalExpense: Double,
    val totalSaved: Double,
    val finalBalance: Double,
    val closedTimestamp: Long = System.currentTimeMillis()
)

