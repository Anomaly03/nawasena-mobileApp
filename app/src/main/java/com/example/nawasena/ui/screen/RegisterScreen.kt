package com.example.nawasena.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.nawasena.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    // PERBAIKAN: Ambil uiState
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Buat Akun Baru", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                enabled = !uiState.isLoading, // PERBAIKAN
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )


            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password") },
                enabled = !uiState.isLoading,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            )

            Button(
                onClick = { viewModel.register(name, email, password, phone) },
                enabled = !uiState.isLoading && name.isNotBlank(), // PERBAIKAN
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Daftar Sekarang")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Sudah punya akun? ")
                Text(text = "Login disini", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { onNavigateToLogin() })
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}