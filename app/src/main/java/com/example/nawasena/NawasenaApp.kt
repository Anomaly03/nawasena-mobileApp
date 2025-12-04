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
import com.example.nawasena.ui.screen.EditProfileScreen
import com.example.nawasena.ui.screen.LoginScreen
import com.example.nawasena.ui.screen.ProfileScreen
import com.example.nawasena.ui.screen.RegisterScreen
import com.example.nawasena.ui.viewmodel.AuthViewModel
import com.example.nawasena.ui.viewmodel.DashboardViewModel
import com.example.nawasena.ui.viewmodel.ProfileViewModel


object Route {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val DETAIL_DESTINATION = "detail/{destinationId}"

    // Routes untuk Bottom Nav
    const val PROFILE = "profile"
    const val HOME = "home"
    const val SEARCH = "search"
    const val FAVORITE = "favorite"

    const val EDIT_PROFILE = "edit_profile"
}

@Composable
fun NawasenaApp(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    profileViewModel: ProfileViewModel,
    // Tambahkan parameter ini
    userLat: Double,
    userLong: Double
) {
    val navController = rememberNavController()
    val authUiState by authViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (authUiState.currentUser != null) Route.DASHBOARD else Route.LOGIN
    ) {

        // --- LOGIN ---
        composable(Route.LOGIN) {
            LaunchedEffect(authUiState.currentUser) {
                if (authUiState.currentUser != null) {
                    navController.navigate(Route.DASHBOARD) { popUpTo(Route.LOGIN) { inclusive = true } }
                }
            }
            LoginScreen(viewModel = authViewModel, onNavigateToRegister = { navController.navigate(Route.REGISTER) })
        }

        // --- REGISTER ---
        composable(Route.REGISTER) {
            LaunchedEffect(authUiState.currentUser) {
                if (authUiState.currentUser != null) {
                    navController.navigate(Route.DASHBOARD) {
                        popUpTo(Route.REGISTER) { inclusive = true }
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                }
            }
            RegisterScreen(viewModel = authViewModel, onNavigateToLogin = { navController.popBackStack() })
        }

        // --- DASHBOARD ---
        composable(Route.DASHBOARD) {
            LaunchedEffect(authUiState.currentUser) {
                if (authUiState.currentUser == null) {
                    navController.navigate(Route.LOGIN) { popUpTo(Route.DASHBOARD) { inclusive = true } }
                }
            }

            DashboardScreen(
                user = authUiState.currentUser,
                viewModel = dashboardViewModel,
                currentLat = userLat,
                currentLong = userLong,
                onDestinationClick = { route ->
                    navController.navigate(route)
                }
            )
        }

        // --- PROFILE (DIUPDATE) ---
        composable(Route.PROFILE) {
            val profileState by profileViewModel.profileState.collectAsState()

            ProfileScreen(
                userProfile = profileState,

                // Saat tombol Edit ditekan, pindah ke halaman Edit
                onNavigateToEdit = {
                    navController.navigate(Route.EDIT_PROFILE)
                },

                // Navigasi Bottom Bar
                onNavigateToHome = {
                    navController.navigate(Route.DASHBOARD) {
                        popUpTo(Route.DASHBOARD) { inclusive = true }
                    }
                },
                onNavigateToSearch = { navController.navigate(Route.SEARCH) },
                onNavigateToFavorite = { navController.navigate(Route.FAVORITE) }
            )
        }

        // --- SEARCH (Placeholder) ---
        composable(Route.SEARCH) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Halaman Search (Coming Soon)")
            }
        }

        // --- FAVORITE (Placeholder) ---
        composable(Route.FAVORITE) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Halaman Favorite (Coming Soon)")
            }
        }

        // --- DETAIL DESTINATION ---
        composable(Route.DETAIL_DESTINATION) { backStackEntry ->
            val destinationId = backStackEntry.arguments?.getString("destinationId")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Detail Destinasi ID: $destinationId")
            }
        }
        composable(Route.EDIT_PROFILE) {
            // Ambil data profile saat ini untuk mengisi form awal
            val profileState by profileViewModel.profileState.collectAsState()

            EditProfileScreen(
                currentProfile = profileState,
                onBack = { navController.popBackStack() }, // Kembali tanpa simpan
                onSave = { newName, newUsername, newPhone, newBirth ->

                    // 1. Panggil ViewModel untuk update ke Room
                    profileViewModel.updateProfile(
                        name = newName,
                        username = newUsername,
                        phone = newPhone,
                        birth = newBirth
                    )

                    // 2. Kembali ke halaman profil setelah simpan
                    navController.popBackStack()
                }
            )
        }
    }
}