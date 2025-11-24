// File Navigasi dan Route.
// Mengatur alur perpindahan antar layar (misalnya, dari Route.LOGIN ke Route.DASHBOARD).

package com.example.nawasena

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Import file dari luar yang sudah dibuat (MVVM)
import com.example.nawasena.ui.screen.LoginScreen
import com.example.nawasena.ui.screen.DashboardScreen


// --- 1. DEFINISI ROUTE ---
object Route {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val DETAIL_DESTINATION = "detail/{destinationId}"
    // ... rute lainnya
}

// --- 2. FUNGSI UTAMA NAVIGASI ---
@Composable
fun NawasenaApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.LOGIN
    ) {
        composable(Route.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.DASHBOARD) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { /* TODO */ }
            )
        }

        composable(Route.DASHBOARD) {
            DashboardScreen( // Panggil DashboardScreen
                onDestinationClick = { destinationId ->
                    navController.navigate("detail/$destinationId")
                }
            )
        }

        composable(Route.DETAIL_DESTINATION) { backStackEntry ->
            val destinationId = backStackEntry.arguments?.getString("destinationId") ?: return@composable
            Text("Detail Destinasi ID: $destinationId")
        }
    }
}