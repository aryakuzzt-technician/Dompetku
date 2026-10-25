
package com.example.dompetku.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dompetku.data.AppSettingsEntity
import com.example.dompetku.ui.viewmodel.DompetViewModel
import java.text.NumberFormat
import java.util.Locale

private fun settingsRupiah(amount: Long): String =
    NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        .format(amount)
        .replace(",00", "")

@Composable
fun SettingsScreen(onBack: () -> Unit = {}) {
    val vm: DompetViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    var salaryText by remember(state.salary) {
        mutableStateOf(state.salary.toString())
    }

    var installmentText by remember { mutableStateOf("810000") }
    var fuelPercent by remember { mutableFloatStateOf(10f) }
    var savingsPercent by remember { mutableFloatStateOf(10f) }
    var emergencyPercent by remember { mutableFloatStateOf(4f) }

    val salary = salaryText.toLongOrNull() ?: 0L
    val fuelBudget = (salary * fuelPercent / 100).toLong()
    val savingsBudget = (salary * savingsPercent / 100).toLong()
    val emergencyBudget = (salary * emergencyPercent / 100).toLong()
    val installment = installmentText.toLongOrNull() ?: 0L

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Pengaturan DompetKu",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedButton(onClick = onBack) {
            Text("Kembali")
        }

        OutlinedTextField(
            value = salaryText,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) salaryText = input
            },
            label = { Text("Gaji bulanan (Rp)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = installmentText,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) installmentText = input
            },
            label = { Text("Nominal cicilan wajib (Rp)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text("Persentase uang bensin: ${fuelPercent.toInt()}%")
        Slider(
            value = fuelPercent,
            onValueChange = { fuelPercent = it },
            valueRange = 0f..40f
        )
        Text("Anggaran bensin: ${settingsRupiah(fuelBudget)}")

        Text("Persentase tabungan wajib: ${savingsPercent.toInt()}%")
        Slider(
            value = savingsPercent,
            onValueChange = { savingsPercent = it },
            valueRange = 0f..50f
        )
        Text("Anggaran tabungan: ${settingsRupiah(savingsBudget)}")

        Text("Persentase dana darurat: ${emergencyPercent.toInt()}%")
        Slider(
            value = emergencyPercent,
            onValueChange = { emergencyPercent = it },
            valueRange = 0f..30f
        )
        Text("Anggaran dana darurat: ${settingsRupiah(emergencyBudget)}")

        val totalAllocation = installment + fuelBudget +
            savingsBudget + emergencyBudget

        Text("Total alokasi: ${settingsRupiah(totalAllocation)}")
        Text(
            "Sisa setelah alokasi: ${
                settingsRupiah(salary - totalAllocation)
            }"
        )

        Button(
            onClick = {
                if (salary > 0L && installment >= 0L) {
                    vm.saveSettings(
                        AppSettingsEntity(
                            monthlySalary = salary,
                            installmentAmount = installment,
                            fuelPercentage = fuelPercent,
                            savingsPercentage = savingsPercent,
                            emergencyPercentage = emergencyPercent
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan Pengaturan")
        }

        if (state.message.isNotBlank()) {
            Text(
                text = state.message,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
