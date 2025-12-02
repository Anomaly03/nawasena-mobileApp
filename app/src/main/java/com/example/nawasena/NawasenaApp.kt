package com.example.nawasena

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nawasena.ui.screen.DashboardScreen
import com.example.nawasena.ui.screen.LoginScreen
import com.example.nawasena.ui.screen.RegisterScreen
import com.example.nawasena.ui.viewmodel.AuthViewModel
import com.example.nawasena.ui.viewmodel.DashboardViewModel // Import ini

object Route {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val DETAIL_DESTINATION = "detail/{destinationId}"
}

@Composable
fun NawasenaApp(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    userLat: Double,  // Tambahkan ini
    userLong: Double  // Tambahkan ini
) {
    val navController = rememberNavController()
    val uiState by authViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (uiState.currentUser != null) Route.DASHBOARD else Route.LOGIN
    ) {

        // --- LOGIN & REGISTER (TIDAK BERUBAH) ---
        composable(Route.LOGIN) {
            LaunchedEffect(uiState.currentUser) {
                if (uiState.currentUser != null) {
                    navController.navigate(Route.DASHBOARD) { popUpTo(Route.LOGIN) { inclusive = true } }
                }
            }
            LoginScreen(viewModel = authViewModel, onNavigateToRegister = { navController.navigate(Route.REGISTER) })
        }

        composable(Route.REGISTER) {
            LaunchedEffect(uiState.currentUser) {
                if (uiState.currentUser != null) {
                    navController.navigate(Route.DASHBOARD) {
                        popUpTo(Route.REGISTER) { inclusive = true }
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                }
            }
            RegisterScreen(viewModel = authViewModel, onNavigateToLogin = { navController.popBackStack() })
        }

        // --- DASHBOARD (DIUPDATE) ---
        composable(Route.DASHBOARD) {
            LaunchedEffect(uiState.currentUser) {
                if (uiState.currentUser == null) {
                    navController.navigate(Route.LOGIN) { popUpTo(Route.DASHBOARD) { inclusive = true } }
                }
            }

            // Kita oper 'dashboardViewModel' ke layarnya
            DashboardScreen(
                user = uiState.currentUser,
                viewModel = dashboardViewModel,
                currentLat = 0.0, // Nanti diisi dari GPS MainActivity
                currentLong = 0.0,
                onLogout = { /* ... */ },
                onDestinationClick = { /* ... */ }
            )
        }

        // --- DETAIL (Placeholder) ---
        composable(Route.DETAIL_DESTINATION) { backStackEntry ->
            val destinationId = backStackEntry.arguments?.getString("destinationId")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Detail Destinasi ID: $destinationId")
            }
        }
    }
}