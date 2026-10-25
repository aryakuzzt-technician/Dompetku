package com.example.dompetku.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dompetku.ui.viewmodel.DompetViewModel

@Composable
fun SettingsScreen(viewModel: DompetViewModel, navController: NavController) {
    val period by viewModel.activePeriod.collectAsState()
    var inputSalary by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val currentSalary = period?.salary ?: 2600000.0

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Pengaturan Gaji", fontWeight = FontWeight.Bold) },
                navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("Kembali") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Ubah Gaji Bulan Ini", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Gaji saat ini: ${formatRupiah(currentSalary)}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = inputSalary,
                onValueChange = { inputSalary = it },
                label = { Text("Nominal Gaji Baru (Rp)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val newSal = inputSalary.toDoubleOrNull()
                    if (newSal != null && newSal >= 810000.0) {
                        viewModel.updateSalary(newSal)
                        navController.popBackStack()
                    } else {
                        errorMessage = "Gaji tidak mencukupi (Minimal Rp810.000 untuk Cicilan Wajib)"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan & Hitung Ulang Alokasi")
            }
        }
    }
}

