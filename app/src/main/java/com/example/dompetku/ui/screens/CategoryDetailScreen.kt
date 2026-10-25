package com.example.dompetku.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dompetku.ui.viewmodel.DompetViewModel

@Composable
fun CategoryDetailScreen(category: String, viewModel: DompetViewModel, navController: NavController) {
    val period by viewModel.activePeriod.collectAsState()
    val transactions by viewModel.currentTransactions.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var txType by remember { mutableStateOf("PEMASUKAN") }

    var inputAmount by remember { mutableStateOf("") }
    var inputNote by remember { mutableStateOf("") }

    var targetCategory by remember { mutableStateOf("SISA") }

    val p = period ?: return
    val categoryTx = transactions.filter { it.category == category }

    val budget = when(category) {
        "CICILAN" -> p.cicilanAmount
        "BENSIN" -> p.bensinBudget
        "TABUNGAN" -> p.tabunganBudget
        "DARURAT" -> p.daruratBudget
        else -> p.sisaBudget
    }

    val balance = when(category) {
        "CICILAN" -> if (p.cicilanStatus) 0.0 else p.cicilanAmount
        "BENSIN" -> p.bensinBalance
        "TABUNGAN" -> p.tabunganBalance
        "DARURAT" -> p.daruratBalance
        else -> p.sisaBalance
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Kategori: $category", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) { Text("Kembali") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Anggaran Awal: ${formatRupiah(budget)}", fontSize = 14.sp)
                    Text("Saldo Saat Ini: ${formatRupiah(balance)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (category == "CICILAN") {
                Button(
                    onClick = { viewModel.payCicilan() },
                    enabled = !p.cicilanStatus,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text(if (p.cicilanStatus) "Sudah Dibayar" else "Saya Sudah Membayar (Rp810.000)")
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { txType = "PEMASUKAN"; showAddDialog = true }, modifier = Modifier.weight(1f)) {
                        Text("Pemasukan")
                    }
                    Button(onClick = { txType = "PENGELUARAN"; showAddDialog = true }, modifier = Modifier.weight(1f)) {
                        Text("Pengeluaran")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { showTransferDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Transfer Ke...")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Riwayat Aktivitas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categoryTx) { tx ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(tx.note, fontWeight = FontWeight.SemiBold)
                                Text(tx.type, fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(
                                text = (if (tx.type == "PEMASUKAN") "+" else "-") + formatRupiah(tx.amount),
                                color = if (tx.type == "PEMASUKAN") Color(0xFF4CAF50) else Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah $txType") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = { inputAmount = it },
                        label = { Text("Nominal (Rp)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputNote,
                        onValueChange = { inputNote = it },
                        label = { Text("Catatan / Keterangan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val amt = inputAmount.toDoubleOrNull() ?: 0.0
                    if (amt > 0 && inputNote.isNotBlank()) {
                        viewModel.addTransaction(category, txType, amt, inputNote)
                        showAddDialog = false
                        inputAmount = ""
                        inputNote = ""
                    }
                }) { Text("Simpan") }
            },
            dismissButton = { OutlinedButton(onClick = { showAddDialog = false }) { Text("Batal") } }
        )
    }

    if (showTransferDialog) {
        AlertDialog(
            onDismissRequest = { showTransferDialog = false },
            title = { Text("Transfer dari $category") },
            text = {
                Column {
                    Text("Pilih Tujuan:")
                    Row {
                        listOf("BENSIN", "TABUNGAN", "DARURAT", "SISA").filter { it != category }.forEach { cat ->
                            RadioButton(selected = targetCategory == cat, onClick = { targetCategory = cat })
                            Text(cat, modifier = Modifier.align(Alignment.CenterVertically))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = { inputAmount = it },
                        label = { Text("Nominal Transfer (Rp)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val amt = inputAmount.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        viewModel.transferBalance(category, targetCategory, amt)
                        showTransferDialog = false
                        inputAmount = ""
                    }
                }) { Text("Transfer") }
            },
            dismissButton = { OutlinedButton(onClick = { showTransferDialog = false }) { Text("Batal") } }
        )
    }
}

