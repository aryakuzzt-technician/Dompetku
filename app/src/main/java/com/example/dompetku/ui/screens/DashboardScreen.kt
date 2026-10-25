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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

private fun rupiah(value: Long): String {
    return NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    ).format(value).replace(",00", "")
}

@Composable
fun DashboardScreen() {
    val gajiBulanIni = 2_500_000L
    val saldoBulanIni = 1_690_000L
    val tabunganWajib = 0L
    val totalSaldoTersisa = 1_690_000L

    val kategori = listOf(
        "Cicilan wajib" to 810_000L,
        "Uang bensin" to 250_000L,
        "Tabungan wajib" to 250_000L,
        "Dana darurat" to 100_000L
    )

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
            Text(
                text = "Ringkasan keuangan bulanan",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        item {
            SummaryCard(
                title = "Gaji Bulan Ini",
                amount = gajiBulanIni
            )
        }

        item {
            SummaryCard(
                title = "Saldo Bulan Ini",
                amount = saldoBulanIni
            )
        }

        item {
            SummaryCard(
                title = "Total Tabungan Wajib",
                amount = tabunganWajib
            )
        }

        item {
            SummaryCard(
                title = "Total Seluruh Saldo Tersisa",
                amount = totalSaldoTersisa
            )
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pembagian Anggaran",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(kategori) { item ->
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.first,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = rupiah(item.second),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            // Detail kategori akan diaktifkan berikutnya.
                        }
                    ) {
                        Text("Detail")
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    // Laporan bulanan akan diaktifkan berikutnya.
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lihat Laporan")
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
                text = rupiah(amount),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
