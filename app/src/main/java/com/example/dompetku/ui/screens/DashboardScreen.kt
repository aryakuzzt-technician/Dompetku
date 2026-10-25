package com.example.dompetku.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dompetku.ui.viewmodel.DompetViewModel
import java.text.NumberFormat
import java.util.Locale

private fun dashboardRupiah(value: Long): String {
    return NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    ).format(value).replace(",00", "")
}

@Composable
fun DashboardScreen() {
    val vm: DompetViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "DompetKu",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Ringkasan keuangan bulanan")
        }

        item {
            SummaryCard(
                title = "Gaji Bulan Ini",
                amount = state.salary
            )
        }

        item {
            SummaryCard(
                title = "Pemasukan Tambahan",
                amount = state.income
            )
        }

        item {
            SummaryCard(
                title = "Total Pengeluaran",
                amount = state.expenses
            )
        }

        item {
            SummaryCard(
                title = "Saldo Bulan Ini",
                amount = state.remainingBalance
            )
        }

        item {
            SummaryCard(
                title = "Total Tabungan Wajib",
                amount = state.mandatorySavings
            )
        }

        item {
            Text(
                text = "Anggaran Bulan Ini",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(state.budgets, key = { it.category }) { budget ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = budget.category,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = dashboardRupiah(budget.amount),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            // Menu kategori dibuka melalui navigasi bawah.
                        }
                    ) {
                        Text("Anggaran")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Pembayaran Cicilan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.installmentPaid) {
                Text(
                    text = "Cicilan bulan ini sudah ditandai lunas.",
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Button(
                    onClick = { vm.markInstallmentPaid() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Saya sudah membayar cicilan")
                }
            }
        }

        if (state.message.isNotBlank()) {
            item {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = dashboardRupiah(amount),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
