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

// --- 1. DEFINISI ROUTE ---
object Route {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val DETAIL_DESTINATION = "detail/{destinationId}"
}

// --- 2. FUNGSI UTAMA NAVIGASI ---
@Composable
fun NawasenaApp(
    viewModel: AuthViewModel // Terima ViewModel dari MainActivity
) {
    val navController = rememberNavController()

    // Kita pantau state user secara global di level navigasi
    // Agar jika user logout dari Dashboard, otomatis terlempar ke Login
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (uiState.currentUser != null) Route.DASHBOARD else Route.LOGIN
    ) {

        // --- SCREEN: LOGIN ---
        composable(Route.LOGIN) {
            // Efek samping: Jika user berhasil login, pindah ke Dashboard
            LaunchedEffect(uiState.currentUser) {
                if (uiState.currentUser != null) {
                    navController.navigate(Route.DASHBOARD) {
                        popUpTo(Route.LOGIN) { inclusive = true } // Hapus history agar tidak bisa back ke login
                    }
                }
            }

            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    navController.navigate(Route.REGISTER)
                }
            )
        }

        // --- SCREEN: REGISTER ---
        composable(Route.REGISTER) {
            // Efek samping: Jika berhasil register, otomatis login & masuk Dashboard
            LaunchedEffect(uiState.currentUser) {
                if (uiState.currentUser != null) {
                    navController.navigate(Route.DASHBOARD) {
                        popUpTo(Route.REGISTER) { inclusive = true }
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                }
            }

            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.popBackStack() // Kembali ke Login
                }
            )
        }

        // --- SCREEN: DASHBOARD ---
        composable(Route.DASHBOARD) {
            // Cek keamanan: Jika user null (logout), lempar balik ke Login
            LaunchedEffect(uiState.currentUser) {
                if (uiState.currentUser == null) {
                    navController.navigate(Route.LOGIN) {
                        popUpTo(Route.DASHBOARD) { inclusive = true }
                    }
                }
            }

            // Pastikan kamu sudah punya DashboardScreen.kt
            // Jika belum, buat dummy sementara (lihat bawah)
            DashboardScreen(
                user = uiState.currentUser,
                onLogout = { viewModel.logout() },
                onDestinationClick = { destinationId ->
                    navController.navigate("detail/$destinationId")
                }
            )
        }

        // --- SCREEN: DETAIL ---
        composable(Route.DETAIL_DESTINATION) { backStackEntry ->
            val destinationId = backStackEntry.arguments?.getString("destinationId")

            // Dummy Detail Screen
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Detail Destinasi ID: $destinationId")
            }
        }
    }
}