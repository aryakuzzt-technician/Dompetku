
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
import com.example.dompetku.data.BudgetEntity
import com.example.dompetku.ui.viewmodel.DompetViewModel
import java.text.NumberFormat
import java.util.Locale

private fun dashboardRupiah(amount: Long): String =
    NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        .format(amount)
        .replace(",00", "")

@Composable
fun DashboardScreen(
    onOpenCategory: (String) -> Unit = {},
    onOpenReports: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val vm: DompetViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "DompetKu",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text("Ringkasan keuangan bulanan")
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DashboardLine("Gaji bulanan", state.salary)
                    DashboardLine("Pemasukan tambahan", state.income)
                    DashboardLine("Pengeluaran", state.expenses)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Saldo tersisa",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = dashboardRupiah(state.remainingBalance),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (state.remainingBalance < 0L) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )

                    DashboardLine(
                        "Akumulasi tabungan wajib",
                        state.mandatorySavings
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Cicilan Wajib",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        if (state.installmentPaid) {
                            "Status: sudah dibayar bulan ini"
                        } else {
                            "Status: belum dibayar bulan ini"
                        }
                    )

                    Button(
                        onClick = { vm.markInstallmentPaid() },
                        enabled = !state.installmentPaid
                    ) {
                        Text(
                            if (state.installmentPaid) {
                                "Sudah Lunas"
                            } else {
                                "Tandai Sudah Dibayar"
                            }
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Anggaran Kategori",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (state.budgets.isEmpty()) {
            item {
                Text("Anggaran belum tersedia.")
            }
        } else {
            items(state.budgets, key = { it.category }) { budget ->
                BudgetCard(
                    budget = budget,
                    onClick = { onOpenCategory(budget.category) }
                )
            }
        }

        item {
            Text(
                text = "Menu Cepat",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
private fun BudgetCard(
    budget: BudgetEntity,
    onClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = budget.category,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text("Anggaran: ${dashboardRupiah(budget.amount)}")

            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buka Kategori")
            }
        }
    }
}

@Composable
private fun DashboardLine(label: String, amount: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(dashboardRupiah(amount))
    }
}
