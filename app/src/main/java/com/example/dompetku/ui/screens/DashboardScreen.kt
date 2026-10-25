
package com.example.dompetku.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dompetku.data.BudgetEntity
import com.example.dompetku.ui.viewmodel.DompetViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    onOpenCategory: (String) -> Unit,
    onOpenReports: () -> Unit,
    onOpenSettings: () -> Unit,
    vm: DompetViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "DompetKu",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Ringkasan keuangan bulanan",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Sisa saldo")
                    Text(
                        text = rupiah(state.remainingBalance),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    SummaryLine("Gaji bulanan", state.salary)
                    SummaryLine("Pemasukan tambahan", state.income)
                    SummaryLine("Total pengeluaran", state.expenses)
                }
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Akumulasi tabungan wajib",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        rupiah(state.mandatorySavings),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Cicilan wajib",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        if (state.installmentPaid) {
                            "Status: sudah dibayar"
                        } else {
                            "Status: belum dibayar"
                        }
                    )

                    Button(
                        onClick = { vm.markInstallmentPaid() },
                        enabled = !state.installmentPaid,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tandai cicilan lunas")
                    }
                }
            }
        }

        item {
            Text(
                "Anggaran bulan ini",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(
            items = state.budgets,
            key = { budget: BudgetEntity ->
                "${budget.monthKey}_${budget.category}"
            }
        ) { budget ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        budget.category,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Anggaran: ${rupiah(budget.amount)}")
                    if (budget.percentage > 0f) {
                        Text("Persentase: ${budget.percentage}%")
                    }

                    Spacer(Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            onOpenCategory(budget.category)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Buka kategori")
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenReports,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Laporan")
                }

                OutlinedButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Pengaturan")
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
private fun SummaryLine(
    label: String,
    amount: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(rupiah(amount))
    }
}

private fun rupiah(amount: Long): String {
    return NumberFormat.getCurrencyInstance(
        Locale("in", "ID")
    ).format(amount)
}
