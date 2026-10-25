
package com.example.dompetku.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dompetku.data.MonthlyReportEntity
import com.example.dompetku.ui.viewmodel.DompetViewModel
import java.text.NumberFormat
import java.util.Locale

private fun reportRupiah(amount: Long): String =
    NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        .format(amount)
        .replace(",00", "")

@Composable
fun ReportScreen(onBack: () -> Unit = {}) {
    val vm: DompetViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Laporan Bulanan",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(onClick = onBack) {
                Text("Kembali")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Ringkasan bulan berjalan")
                ReportLine("Gaji bulanan", state.salary)
                ReportLine("Pemasukan tambahan", state.income)
                ReportLine("Total pengeluaran", state.expenses)
                ReportLine(
                    "Saldo tersisa",
                    state.remainingBalance,
                    emphasize = true
                )
                ReportLine(
                    "Akumulasi tabungan wajib",
                    state.mandatorySavings
                )
            }
        }

        Text(
            text = "Rincian Transaksi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        if (state.transactions.isEmpty()) {
            Text("Belum ada transaksi bulan ini.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.transactions, key = { it.id }) { transaction ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                transaction.category,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "${transaction.type}: ${
                                    reportRupiah(transaction.amount)
                                }"
                            )
                            if (transaction.note.isNotBlank()) {
                                Text(transaction.note)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { vm.closeMonth() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Closing Bulanan")
        }

        if (state.message.isNotBlank()) {
            Text(
                text = state.message,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ReportLine(
    label: String,
    amount: Long,
    emphasize: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = reportRupiah(amount),
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal
        )
    }
}
