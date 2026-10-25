
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dompetku.data.TransactionEntity
import com.example.dompetku.ui.viewmodel.DompetViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CategoryDetailScreen(
    category: String,
    onBack: () -> Unit,
    vm: DompetViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()

    var amountText by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var isIncome by rememberSaveable { mutableStateOf(false) }

    val categoryTransactions = state.transactions.filter {
        it.category == category
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = category,
                style = MaterialTheme.typography.headlineMedium
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
                        "Catat transaksi",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isIncome = false }
                        ) {
                            Text(
                                if (!isIncome) "✓ Pengeluaran"
                                else "Pengeluaran"
                            )
                        }

                        OutlinedButton(
                            onClick = { isIncome = true }
                        ) {
                            Text(
                                if (isIncome) "✓ Pemasukan"
                                else "Pemasukan"
                            )
                        }
                    }

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { value ->
                            amountText = value.filter { it.isDigit() }
                        },
                        label = { Text("Nominal (Rp)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Catatan (opsional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val amount = amountText.toLongOrNull()
                            if (amount != null && amount > 0L) {
                                if (isIncome) {
                                    vm.addIncome(category, amount, note)
                                } else {
                                    vm.addExpense(category, amount, note)
                                }

                                amountText = ""
                                note = ""
                            }
                        },
                        enabled = amountText.toLongOrNull()?.let {
                            it > 0L
                        } == true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simpan transaksi")
                    }
                }
            }
        }

        item {
            Text(
                "Riwayat transaksi",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (categoryTransactions.isEmpty()) {
            item {
                Text("Belum ada transaksi untuk kategori ini.")
            }
        } else {
            items(
                items = categoryTransactions,
                key = { transaction: TransactionEntity ->
                    transaction.id
                }
            ) { transaction ->
                Card(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            if (transaction.type == "INCOME") {
                                "Pemasukan"
                            } else {
                                "Pengeluaran"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(rupiah(transaction.amount))

                        if (transaction.note.isNotBlank()) {
                            Text("Catatan: ${transaction.note}")
                        }

                        Text(
                            formatDate(transaction.dateMillis),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun rupiah(amount: Long): String {
    return NumberFormat.getCurrencyInstance(
        Locale("in", "ID")
    ).format(amount)
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat(
        "dd MMM yyyy, HH:mm",
        Locale("id", "ID")
    ).format(Date(timestamp))
}
