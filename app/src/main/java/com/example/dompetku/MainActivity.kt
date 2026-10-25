package com.example.dompetku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.dompetku.ui.screens.*
import com.example.dompetku.ui.theme.DompetKuTheme
import com.example.dompetku.ui.viewmodel.DompetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DompetKuTheme {
                val viewModel: DompetViewModel = viewModel()
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") {
                        DashboardScreen(viewModel, navController)
                    }
                    composable("settings") {
                        SettingsScreen(viewModel, navController)
                    }
                    composable("reports") {
                        ReportScreen(viewModel, navController)
                    }
                    composable("category/{categoryName}") { backStackEntry ->
                        val catName = backStackEntry.arguments?.getString("categoryName") ?: "SISA"
                        CategoryDetailScreen(catName, viewModel, navController)
                    }
                }
            }
        }
    }
}

