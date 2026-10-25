package com.example.dompetku.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dompetku.data.ActivePeriodEntity
import com.example.dompetku.ui.viewmodel.DompetViewModel
import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "IDR"))
    return format.format(amount).replace("IDR", "Rp").replace(",00", "")
}

@Composable
fun DashboardScreen(viewModel: DompetViewModel, navController: NavController) {
    val period by viewModel.activePeriod.collectAsState()
    var showCloseDialog by remember { mutableStateOf(false) }

    if (period == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val p = period!!
    val totalSaldoTersisa = p.bensinBalance + p.tabunganBalance + p.daruratBalance + p.sisaBalance + p.totalAccumulatedSisa

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("DompetKu (${p.monthYear})", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = { navController.navigate("settings") }) {
                        Text("Pengaturan Gaji")
                    }
                    TextButton(onClick = { navController.navigate("reports") }) {
                        Text("Laporan")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("RINGKASAN UTAMA", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Gaji Bulan Ini", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                Text(formatRupiah(p.salary), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Column {
                                Text("Saldo Bulan Ini", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                Text(formatRupiah(p.bensinBalance + p.tabunganBalance + p.daruratBalance + p.sisaBalance), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Total Tabungan Wajib", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                Text(formatRupiah(p.totalAccumulatedTabungan + p.tabunganBalance), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Column {
                                Text("Total Seluruh Saldo", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                Text(formatRupiah(totalSaldoTersisa), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            item { Text("Kartu Anggaran Kategori", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

            item {
                CategoryCard(
                    title = "Cicilan Wajib",
                    budget = p.cicilanAmount,
                    balance = if (p.cicilanStatus) 0.0 else p.cicilanAmount,
                    statusText = if (p.cicilanStatus) "Sudah Dibayar" else "Belum Dibayar",
                    statusColor = if (p.cicilanStatus) Color.Red else Color(0xFF4CAF50),
                    onClick = { navController.navigate("category/CICILAN") }
                )
            }

            item {
                CategoryCard(
                    title = "Uang Bensin",
                    budget = p.bensinBudget,
                    balance = p.bensinBalance,
                    onClick = { navController.navigate("category/BENSIN") }
                )
            }

            item {
                CategoryCard(
                    title = "Tabungan Wajib",
                    budget = p.tabunganBudget,
                    balance = p.tabunganBalance,
                    onClick = { navController.navigate("category/TABUNGAN") }
                )
            }

            item {
                CategoryCard(
                    title = "Dana Darurat",
                    budget = p.daruratBudget,
                    balance = p.daruratBalance,
                    onClick = { navController.navigate("category/DARURAT") }
                )
            }

            item {
                CategoryCard(
                    title = "Sisa Saldo Operasional",
                    budget = p.sisaBudget,
                    balance = p.sisaBalance,
                    onClick = { navController.navigate("category/SISA") }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showCloseDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Tutup Bulan Ini (Closing)", color = Color.White)
                }
            }
        }
    }

    if (showCloseDialog) {
        AlertDialog(
            onDismissRequest = { showCloseDialog = false },
            title = { Text("Konfirmasi Closing Bulanan") },
            text = { Text("Seluruh sisa saldo dan tabungan bulan ini akan diarsipkan ke Total Akumulasi. Anda akan memulai periode bulan baru.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.closeMonth("November 2026")
                    showCloseDialog = false
                }) {
                    Text("Proses Closing")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCloseDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun CategoryCard(
    title: String,
    budget: Double,
    balance: Double,
    statusText: String? = null,
    statusColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Anggaran: ${formatRupiah(budget)}", fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatRupiah(balance), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                if (statusText != null) {
                    Text(statusText, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

