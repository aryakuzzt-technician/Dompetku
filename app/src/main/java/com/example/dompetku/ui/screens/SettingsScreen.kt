
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dompetku.data.AppSettingsEntity
import com.example.dompetku.ui.viewmodel.DompetViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: DompetViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()

    var salaryText by rememberSaveable(state.salary) {
        mutableStateOf(state.salary.toString())
    }

    var installmentText by rememberSaveable {
        mutableStateOf("810000")
    }

    var fuelPercentage by rememberSaveable {
        mutableFloatStateOf(10f)
    }

    var savingsPercentage by rememberSaveable {
        mutableFloatStateOf(10f)
    }

    var emergencyPercentage by rememberSaveable {
        mutableFloatStateOf(4f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "Pengaturan keuangan",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedButton(onClick = onBack) {
            Text("Kembali")
        }

        Card(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Pendapatan dan cicilan",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = salaryText,
                    onValueChange = {
                        salaryText = it.filter(Char::isDigit)
                    },
                    label = { Text("Gaji bulanan (Rp)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = installmentText,
                    onValueChange = {
                        installmentText = it.filter(Char::isDigit)
                    },
                    label = { Text("Cicilan bulanan (Rp)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Alokasi gaji",
                    style = MaterialTheme.typography.titleLarge
                )

                PercentageSetting(
                    title = "Uang bensin",
                    value = fuelPercentage,
                    onValueChange = { fuelPercentage = it }
                )

                PercentageSetting(
                    title = "Tabungan wajib",
                    value = savingsPercentage,
                    onValueChange = { savingsPercentage = it }
                )

                PercentageSetting(
                    title = "Dana darurat",
                    value = emergencyPercentage,
                    onValueChange = { emergencyPercentage = it }
                )

                val salary = salaryText.toLongOrNull() ?: 0L

                Text("Bensin: ${rupiah((salary * fuelPercentage / 100).toLong())}")
                Text("Tabungan: ${rupiah((salary * savingsPercentage / 100).toLong())}")
                Text("Dana darurat: ${rupiah((salary * emergencyPercentage / 100).toLong())}")

                val totalPercentage =
                    fuelPercentage + savingsPercentage + emergencyPercentage

                Text(
                    "Total alokasi: ${"%.0f".format(Locale.US, totalPercentage)}%"
                )

                if (totalPercentage > 100f) {
                    Text(
                        "Peringatan: total persentase melebihi 100%.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Button(
            onClick = {
                val salary = salaryText.toLongOrNull()
                val installment = installmentText.toLongOrNull()

                if (
                    salary != null &&
                    salary > 0L &&
                    installment != null &&
                    installment >= 0L &&
                    fuelPercentage + savingsPercentage +
                    emergencyPercentage <= 100f
                ) {
                    vm.saveSettings(
                        AppSettingsEntity(
                            monthlySalary = salary,
                            installmentAmount = installment,
                            fuelPercentage = fuelPercentage,
                            savingsPercentage = savingsPercentage,
                            emergencyPercentage = emergencyPercentage
                        )
                    )
                }
            },
            enabled = salaryText.toLongOrNull()?.let { it > 0L } == true &&
                installmentText.toLongOrNull()?.let { it >= 0L } == true &&
                fuelPercentage + savingsPercentage +
                emergencyPercentage <= 100f,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan pengaturan")
        }

        if (state.message.isNotBlank()) {
            Text(
                state.message,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun PercentageSetting(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title)
            Text("${"%.0f".format(Locale.US, value)}%")
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f
        )
    }
}

private fun rupiah(amount: Long): String {
    return NumberFormat.getCurrencyInstance(
        Locale("in", "ID")
    ).format(amount)
}
