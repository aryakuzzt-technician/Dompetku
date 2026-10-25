package com.example.dompetku.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dompetku.ui.viewmodel.DompetViewModel

@Composable
fun ReportScreen(viewModel: DompetViewModel, navController: NavController) {
    val reports by viewModel.allReports.collectAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Laporan Bulanan", fontWeight = FontWeight.Bold) },
                navigationIcon = { TextButton(onClick = { navController.popBackStack() }) { Text("Kembali") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (reports.isEmpty()) {
                Text("Belum ada laporan bulan yang ditutup.", fontSize = 14.sp)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(reports) { r ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Periode: ${r.monthYear}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Text("Gaji: ${formatRupiah(r.salary)}")
                                Text("Pengeluaran Cicilan: ${formatRupiah(r.totalExpense)}")
                                Text("Berhasil Ditabung: ${formatRupiah(r.totalSaved)}", fontWeight = FontWeight.SemiBold)
                                Text("Saldo Akhir Ditutup: ${formatRupiah(r.finalBalance)}")
                            }
                        }
                    }
                }
            }
        }
    }
}

