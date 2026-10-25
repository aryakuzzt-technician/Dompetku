package com.example.dompetku.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey
    val category: String,
    val amount: Long,
    val percentage: Float = 0f,
    val monthKey: String
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val amount: Long,
    val type: String,
    val note: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val monthKey: String
)

@Entity(tableName = "installments")
data class InstallmentEntity(
    @PrimaryKey
    val id: Int = 1,
    val title: String = "Cicilan wajib",
    val amount: Long = 810_000L,
    val isPaid: Boolean = false,
    val monthKey: String
)

@Entity(tableName = "monthly_reports")
data class MonthlyReportEntity(
    @PrimaryKey
    val monthKey: String,
    val totalIncome: Long,
    val totalExpense: Long,
    val totalMandatorySavings: Long,
    val remainingBalance: Long,
    val isClosed: Boolean = true
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val monthlySalary: Long = 2_500_000L,
    val installmentAmount: Long = 810_000L,
    val fuelPercentage: Float = 10f,
    val savingsPercentage: Float = 10f,
    val emergencyPercentage: Float = 4f,
    val accumulatedMandatorySavings: Long = 0L,
    val accumulatedRemainingBalance: Long = 0L
)
