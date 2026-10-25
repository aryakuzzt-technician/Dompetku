package com.example.dompetku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dompetku.ui.screens.CategoryDetailScreen
import com.example.dompetku.ui.screens.DashboardScreen
import com.example.dompetku.ui.screens.ReportScreen
import com.example.dompetku.ui.screens.SettingsScreen
import com.example.dompetku.ui.theme.DompetKuTheme
import com.example.dompetku.ui.viewmodel.DompetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DompetKuTheme {
                val vm: DompetViewModel = viewModel()

                var page by remember {
                    mutableStateOf("dashboard")
                }

                var selectedCategory by remember {
                    mutableStateOf("Cicilan wajib")
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = page == "dashboard",
                                onClick = { page = "dashboard" },
                                icon = {
                                    Icon(
                                        Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Dashboard"
                                    )
                                },
                                label = { Text("Beranda") }
                            )

                            NavigationBarItem(
                                selected = page == "category",
                                onClick = { page = "category" },
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
                    Surface(
                        modifier = Modifier.padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (page) {
                            "dashboard" -> DashboardScreen()

                            "category" -> CategoryDetailScreen(
                                category = selectedCategory,
                                onBack = { page = "dashboard" }
                            )

                            "reports" -> ReportScreen(
                                onBack = { page = "dashboard" }
                            )

                            "settings" -> SettingsScreen(
                                onBack = { page = "dashboard" }
                            )
                        }
                    }
                }
            }
        }
    }
}
