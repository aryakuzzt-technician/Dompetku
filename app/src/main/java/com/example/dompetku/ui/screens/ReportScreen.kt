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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

private fun reportRupiah(value: Long): String {
    return NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    ).format(value).replace(",00", "")
}

@Composable
fun ReportScreen(
    onBack: () -> Unit = {}
) {
    var monthIndex by remember { mutableStateOf(0) }

    val months = listOf(
        "Januari", "Februari", "Maret",
        "April", "Mei", "Juni",
        "Juli", "Agustus", "September",
        "Oktober", "November", "Desember"
    )

    val year = 2026
    val month = months[monthIndex]

    val income = 2_500_000L
    val expenses = 810_000L
    val savings = 250_000L
    val remaining = income - expenses - savings

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
            text = "Laporan Bulanan",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "$month $year",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            monthIndex =
                                if (monthIndex == 0) 11
                                else monthIndex - 1
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Bulan sebelumnya")
                    }

                    Button(
                        onClick = {
                            monthIndex =
                                if (monthIndex == 11) 0
                                else monthIndex + 1
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Bulan berikutnya")
                    }
                }
            }
        }

        ReportItem("Total pemasukan", income)
        ReportItem("Total pengeluaran", expenses)
        ReportItem("Tabungan wajib", savings)
        ReportItem("Sisa dana", remaining)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Catatan",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Angka pada halaman ini masih contoh. Setelah database dan sistem transaksi terhubung, laporan akan menggunakan data nyata setiap bulan."
        )
    }
}

@Composable
private fun ReportItem(
    title: String,
    amount: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = reportRupiah(amount),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
