package com.example.nawasena.ui.screen

import com.example.nawasena.ui.theme.BottomTriangle
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nawasena.ui.viewmodel.AuthViewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType

@Composable // Hapus @OptIn jika error, atau biarkan jika Android Studio minta
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Latar belakang putih untuk area atas
    ) {

        // 1. LAPISAN BAWAH: BLOK SEGITIGA DENGAN SHADOW
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.BottomCenter)
        ) {
            // Layer Segitiga Asli (hanya satu Box yang diperlukan)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .align(Alignment.BottomCenter) // Pastikan ini juga di-align jika Box luar adalah root Box

                    // 1. Terapkan SHADOW dulu, berikan bentuknya (BottomTriangleShape)
                    .shadow(
                        elevation = 12.dp, // Ketinggian bayangan
                        shape = BottomTriangle(peakHeightDp = 100f, cornerRadiusDp = 30f)
                    )

                    // 2. Kemudian, lakukan CLIPPING dengan bentuk yang sama
                    .clip(BottomTriangle(peakHeightDp = 100f, cornerRadiusDp = 30f))

                    // 3. Berikan warna latar belakang (Biru gelap Anda)
                    .background(Color(0xFF3C30A8))
            )
        }
        }            // 2. LAPISAN ATAS: KONTEN LOGIN UTAMA
                Scaffold(
                    // Scaffold harus transparan agar Box hijau di belakang terlihat
                    containerColor = Color.Transparent
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // --- JUDUL ---
                        Text(
                            "Nawasena",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize=36.sp,
                                fontWeight = FontWeight.Light
                            )
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        // --- TEXT FIELDS ---
                        // Email
                        OutlinedTextField(
                            value = email,
                            textStyle = androidx.compose.ui.text.TextStyle(color = androidx.compose.ui.graphics.Color.Black),
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        )
                        // Password
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Kata Sandi") },
                            textStyle = androidx.compose.ui.text.TextStyle(color = androidx.compose.ui.graphics.Color.Black),
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                val description = if (passwordVisible) "Sembunyikan kata sandi" else "Tampilkan kata sandi"

                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = description)
                                }
                            }
                        )

                        // --- TOMBOL LOGIN ---
                        Button(
                            onClick = { viewModel.login(email, password) },
                            enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3C30A8),
                                contentColor = Color.White
                            )
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Loading...")
                            } else {
                                Text("Sign In")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- LINK REGISTER ---
                        Row {
                            Text("Don't have an account? ")
                            Text(
                                text = "Register",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onNavigateToRegister() }
                            )
                        }

                    }
                }
            }