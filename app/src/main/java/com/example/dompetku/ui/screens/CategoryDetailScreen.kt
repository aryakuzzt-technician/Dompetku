package com.example.dompetku.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

private fun formatRupiah(value: Long): String {
    return NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    ).format(value).replace(",00", "")
}

@Composable
fun CategoryDetailScreen(
    category: String,
    onBack: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val budget = when (category) {
        "Cicilan wajib" -> 810_000L
        "Uang bensin" -> 250_000L
        "Tabungan wajib" -> 250_000L
        "Dana darurat" -> 100_000L
        else -> 0L
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(onClick = onBack) {
            Text("← Kembali")
        }

        Text(
            text = category,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Anggaran kategori")
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatRupiah(budget),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = "Catat Transaksi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                    amount = input
                }
            },
            label = { Text("Nominal (Rupiah)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Keterangan transaksi") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    message = "Form pemasukan siap disambungkan ke database."
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Pemasukan")
            }

            Button(
                onClick = {
                    message = "Form pengeluaran siap disambungkan ke database."
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Pengeluaran")
            }
        }

        OutlinedButton(
            onClick = {
                message = "Fitur pemindahan saldo akan disambungkan nanti."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pindahkan Saldo")
        }

        if (message.isNotBlank()) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "Riwayat Transaksi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Belum ada riwayat transaksi."
        )
    }
}
