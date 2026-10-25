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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

private fun rupiahSettings(value: Long): String {
    return NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    ).format(value).replace(",00", "")
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {}
) {
    var salaryText by remember { mutableStateOf("2500000") }
    var fuelPercent by remember { mutableFloatStateOf(10f) }
    var savingsPercent by remember { mutableFloatStateOf(10f) }
    var emergencyPercent by remember { mutableFloatStateOf(4f) }
    var message by remember { mutableStateOf("") }

    val salary = salaryText.toLongOrNull() ?: 0L
    val installment = 810_000L
    val fuel = (salary * fuelPercent / 100).toLong()
    val savings = (salary * savingsPercent / 100).toLong()
    val emergency = (salary * emergencyPercent / 100).toLong()
    val remaining = salary - installment - fuel - savings - emergency

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedButton(onClick = onBack) {
            Text("← Kembali")
        }

        Text(
            text = "Pengaturan Anggaran",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = salaryText,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                    salaryText = input
                }
            },
            label = { Text("Gaji bulanan (Rupiah)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Cicilan wajib",
                    fontWeight = FontWeight.SemiBold
                )
                Text(rupiahSettings(installment))

                BudgetSlider(
                    title = "Uang bensin",
                    percent = fuelPercent,
                    onChange = { fuelPercent = it },
                    salary = salary
                )

                BudgetSlider(
                    title = "Tabungan wajib",
                    percent = savingsPercent,
                    onChange = { savingsPercent = it },
                    salary = salary
                )

                BudgetSlider(
                    title = "Dana darurat",
                    percent = emergencyPercent,
                    onChange = { emergencyPercent = it },
                    salary = salary
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Perkiraan Pembagian",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                SummaryLine("Gaji", salary)
                SummaryLine("Cicilan wajib", installment)
                SummaryLine("Uang bensin", fuel)
                SummaryLine("Tabungan wajib", savings)
                SummaryLine("Dana darurat", emergency)
                SummaryLine("Sisa untuk kebutuhan lain", remaining)
            }
        }

        Button(
            onClick = {
                message = "Pengaturan dihitung sementara. Penyimpanan permanen akan disambungkan ke database."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan Pengaturan")
        }

        if (message.isNotBlank()) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun BudgetSlider(
    title: String,
    percent: Float,
    onChange: (Float) -> Unit,
    salary: Long
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title)
            Text("${percent.toInt()}%")
        }

        Slider(
            value = percent,
            onValueChange = onChange,
            valueRange = 0f..50f
        )

        Text(
            text = rupiahSettings((salary * percent / 100).toLong()),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SummaryLine(
    title: String,
    amount: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(1.dp))
        Text(rupiahSettings(amount))
    }
}
