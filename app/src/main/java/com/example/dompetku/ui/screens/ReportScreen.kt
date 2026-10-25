
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dompetku.data.TransactionEntity
import com.example.dompetku.ui.viewmodel.DompetViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportScreen(
    onBack: () -> Unit,
    vm: DompetViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()
    var showClosingDialog by remember { mutableStateOf(false) }

    val monthLabel = SimpleDateFormat(
        "MMMM yyyy",
        Locale("id", "ID")
    ).format(Date())

    if (showClosingDialog) {
        AlertDialog(
            onDismissRequest = { showClosingDialog = false },
            title = { Text("Closing bulan") },
            text = {
                Text(
                    "Tutup laporan $monthLabel? " +
                        "Setelah ditutup, transaksi bulan ini akan dikunci."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClosingDialog = false
                        vm.closeMonth()
                    }
                ) {
                    Text("Tutup bulan")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClosingDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Laporan keuangan",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                monthLabel,
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedButton(onClick = onBack) {
                Text("Kembali")
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Ringkasan bulan ini",
                        style = MaterialTheme.typography.titleLarge
                    )
                    ReportLine("Gaji bulanan", state.salary)
                    ReportLine("Pemasukan tambahan", state.income)
                    ReportLine("Total pengeluaran", state.expenses)

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "Sisa saldo",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        rupiah(state.remainingBalance),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text("Tabungan wajib: ${rupiah(state.mandatorySavings)}")
                }
            }
        }

        item {
            Button(
                onClick = { showClosingDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Closing Bulan")
            }
        }

        if (state.message.isNotBlank()) {
            item {
                Text(
                    state.message,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            Text(
                "Riwayat transaksi",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (state.transactions.isEmpty()) {
            item {
                Text("Belum ada transaksi bulan ini.")
            }
        } else {
            items(
                items = state.transactions,
                key = { transaction: TransactionEntity -> transaction.id }
            ) { transaction ->
                Card(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                transaction.category,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                if (transaction.type == "INCOME") {
                                    "+${rupiah(transaction.amount)}"
                                } else {
                                    "-${rupiah(transaction.amount)}"
                                }
                            )
                        }

                        if (transaction.note.isNotBlank()) {
                            Text("Catatan: ${transaction.note}")
                        }

                        Text(
                            SimpleDateFormat(
                                "dd MMM yyyy, HH:mm",
                                Locale("id", "ID")
                            ).format(Date(transaction.dateMillis)),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportLine(
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
