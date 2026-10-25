package com.example.dompetku.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DompetDao {
    @Query("SELECT * FROM active_period WHERE id = 1")
    fun getActivePeriod(): Flow<ActivePeriodEntity?>

    @Query("SELECT * FROM active_period WHERE id = 1")
    suspend fun getActivePeriodDirect(): ActivePeriodEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateActivePeriod(period: ActivePeriodEntity)

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE monthYear = :monthYear ORDER BY timestamp DESC")
    fun getTransactionsByMonth(monthYear: String): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insertReport(report: MonthlyReportEntity)

    @Query("SELECT * FROM monthly_reports ORDER BY closedTimestamp DESC")
    fun getAllReports(): Flow<List<MonthlyReportEntity>>

    @Transaction
    suspend fun executeTransfer(
        sourceCategory: String,
        targetCategory: String,
        amount: Double,
        monthYear: String,
        currentPeriod: ActivePeriodEntity
    ) {
        var bB = currentPeriod.bensinBalance
        var tB = currentPeriod.tabunganBalance
        var dB = currentPeriod.daruratBalance
        var sB = currentPeriod.sisaBalance

        when (sourceCategory) {
            "BENSIN" -> bB -= amount
            "TABUNGAN" -> tB -= amount
            "DARURAT" -> dB -= amount
            "SISA" -> sB -= amount
        }

        when (targetCategory) {
            "BENSIN" -> bB += amount
            "TABUNGAN" -> tB += amount
            "DARURAT" -> dB += amount
            "SISA" -> sB += amount
        }

        val updated = currentPeriod.copy(
            bensinBalance = bB,
            tabunganBalance = tB,
            daruratBalance = dB,
            sisaBalance = sB
        )

        insertOrUpdateActivePeriod(updated)
        insertTransaction(
            TransactionEntity(
                monthYear = monthYear,
                category = sourceCategory,
                type = "TRANSFER",
                amount = amount,
                note = "Transfer ke $targetCategory"
            )
        )
    }
}

