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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dompetku.ui.viewmodel.DompetViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun detailRupiah(value: Long): String {
    return NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    ).format(value).replace(",00", "")
}

@Composable
fun CategoryDetailScreen(
    category: String,
    onBack: () -> Unit
) {
    val vm: DompetViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val categoryBudget = state.budgets
        .firstOrNull { it.category == category }?.amount ?: 0L

    val categoryTransactions = state.transactions
        .filter { it.category == category }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedButton(onClick = onBack) {
                Text("← Kembali")
            }

            Text(
                text = category,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Anggaran kategori")
                    Text(
                        text = detailRupiah(categoryBudget),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Text(
                text = "Catat Transaksi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) amount = input
                },
                label = { Text("Nominal rupiah") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Keterangan") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val value = amount.toLongOrNull()
                        if (value != null && value > 0L) {
                            vm.addIncome(category, value, note)
                            amount = ""
                            note = ""
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Pemasukan")
                }

                Button(
                    onClick = {
                        val value = amount.toLongOrNull()
                        if (value != null && value > 0L) {
                            vm.addExpense(category, value, note)
                            amount = ""
                            note = ""
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Pengeluaran")
                }
            }

            if (state.message.isNotBlank()) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Riwayat Transaksi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (categoryTransactions.isEmpty()) {
            item {
                Text("Belum ada transaksi pada kategori ini.")
            }
        } else {
            items(categoryTransactions, key = { it.id }) { transaction ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = transaction.type,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(detailRupiah(transaction.amount))
                        if (transaction.note.isNotBlank()) {
                            Text(transaction.note)
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
