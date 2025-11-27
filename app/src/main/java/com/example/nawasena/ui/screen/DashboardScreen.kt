package com.example.nawasena.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nawasena.data.model.User

@Composable
fun DashboardScreen(
    user: User?,
    onLogout: () -> Unit,
    onDestinationClick: (String) -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Selamat Datang,", style = MaterialTheme.typography.headlineSmall)
            // Tampilkan nama user (atau 'Guest' jika null)
            Text(text = user?.name ?: "Pengguna", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onLogout) {
                Text("Keluar (Logout)")
            }
        }
    }
}