package com.example.nawasena.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.nawasena.data.local.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    currentProfile: UserProfile?,
    onSave: (String, String, String, String) -> Unit, // Callback: Name, Username, Phone, Birth
    onBack: () -> Unit
) {
    // State untuk menampung input user
    // Kita inisialisasi dengan data lama agar tidak kosong
    var name by remember { mutableStateOf(currentProfile?.name ?: "") }
    var username by remember { mutableStateOf(currentProfile?.username ?: "") }
    var phone by remember { mutableStateOf(currentProfile?.phoneNumber ?: "") }
    var birthDate by remember { mutableStateOf(currentProfile?.birthDate ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Agar bisa discroll jika keyboard muncul
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Input Nama
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Input Username
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Input No HP
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Nomor Telepon") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            // Input Tanggal Lahir
            // (Idealnya pakai DatePicker, tapi biar simpel pakai Text dulu)
            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Tanggal Lahir (DD-MM-YYYY)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Simpan
            Button(
                onClick = {
                    // Panggil fungsi onSave dengan data terbaru
                    onSave(name, username, phone, birthDate)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3C30A8))
            ) {
                Text("Simpan Perubahan")
            }
        }
    }
}