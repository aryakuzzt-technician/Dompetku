
package com.example.dompetku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dompetku.ui.screens.CategoryDetailScreen
import com.example.dompetku.ui.screens.DashboardScreen
import com.example.dompetku.ui.screens.ReportScreen
import com.example.dompetku.ui.screens.SettingsScreen
import com.example.dompetku.ui.theme.DompetKuTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DompetKuTheme {
                var page by rememberSaveable {
                    mutableStateOf("dashboard")
                }

                var selectedCategory by rememberSaveable {
                    mutableStateOf("")
                }

                val categories = listOf(
                    "Cicilan wajib",
                    "Uang bensin",
                    "Tabungan wajib",
                    "Dana darurat",
                    "Kebutuhan harian",
                    "Orang tua",
                    "Makanan kucing",
                    "Saldo bebas"
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = page == "dashboard",
                                onClick = { page = "dashboard" },
                                icon = {
                                    Icon(
                                        Icons.Default.Home,
                                        contentDescription = "Beranda"
                                    )
                                },
                                label = { Text("Beranda") }
                            )

                            NavigationBarItem(
                                selected = page == "categories",
                                onClick = { page = "categories" },
                                icon = {
                                    Icon(
                                        Icons.Default.Category,
                                        contentDescription = "Kategori"
                                    )
                                },
                                label = { Text("Kategori") }
                            )

                            NavigationBarItem(
                                selected = page == "reports",
                                onClick = { page = "reports" },
                                icon = {
                                    Icon(
                                        Icons.Default.Assessment,
                                        contentDescription = "Laporan"
                                    )
                                },
                                label = { Text("Laporan") }
                            )

                            NavigationBarItem(
                                selected = page == "settings",
                                onClick = { page = "settings" },
                                icon = {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = "Pengaturan"
                                    )
                                },
                                label = { Text("Pengaturan") }
                            )
                        }
                    }
                ) { innerPadding ->
                    when (page) {
                        "dashboard" -> {
                            DashboardScreen(
                                onOpenCategory = { category ->
                                    selectedCategory = category
                                    page = "detail"
                                },
                                onOpenReports = {
                                    page = "reports"
                                },
                                onOpenSettings = {
                                    page = "settings"
                                }
                            )
                        }

                        "categories" -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                                    .padding(horizontal = 16.dp),
                                contentPadding = PaddingValues(
                                    top = 16.dp,
                                    bottom = 16.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                item {
                                    Text(
                                        "Kategori keuangan",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }

                                items(categories.size) { index ->
                                    val category = categories[index]

                                    Button(
                                        onClick = {
                                            selectedCategory = category
                                            page = "detail"
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(category)
                                    }
                                }
                            }
                        }

                        "detail" -> {
                            CategoryDetailScreen(
                                category = selectedCategory,
                                onBack = {
                                    page = "categories"
                                }
                            )
                        }

                        "reports" -> {
                            ReportScreen(
                                onBack = {
                                    page = "dashboard"
                                }
                            )
                        }

                        "settings" -> {
                            SettingsScreen(
                                onBack = {
                                    page = "dashboard"
                                }
                            )
                        }

                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                                    .padding(16.dp)
                            ) {
                                Text("Halaman tidak ditemukan.")
                                Button(
                                    onClick = { page = "dashboard" }
                                ) {
                                    Text("Kembali ke beranda")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
