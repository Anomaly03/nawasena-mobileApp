// Menampilkan semua elemen layout (Horizontal Scroll, List Lokal).
// Menerima callback onDestinationClick untuk memberi tahu NawasenaApp (Navigasi) agar pindah ke layar Detail.

package com.example.nawasena.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onDestinationClick: (String) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard Nawasena") }) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Text("Konten Dashboard siap!", modifier = Modifier.padding(16.dp))
            Button(onClick = { onDestinationClick("101") }) {
                Text("Coba Navigasi ke Detail Destinasi (ID 101)")
            }
        }
    }
}