package com.example.nawasena

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.nawasena.data.repository.AuthRepository
import com.example.nawasena.ui.theme.NawasenaTheme
import com.example.nawasena.ui.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. SIAPKAN BAHAN (DEPENDENCIES)
        // Kita inisialisasi Firebase di sini agar bisa dipakai di seluruh aplikasi
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // 2. RAKIT REPOSITORY
        // Repository butuh auth & firestore
        val authRepository = AuthRepository(auth, firestore)

        // 3. RAKIT VIEWMODEL
        // ViewModel butuh repository
        val authViewModel = AuthViewModel(authRepository)

        setContent {
            NawasenaTheme {
                // 4. JALANKAN APLIKASI
                // Kita oper viewModel yang sudah jadi ke Navigasi utama
                NawasenaApp(viewModel = authViewModel)
            }
        }
    }
}