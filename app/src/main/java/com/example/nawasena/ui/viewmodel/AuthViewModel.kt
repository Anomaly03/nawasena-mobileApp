// Mengelola State: Menyimpan status login (isLoading, isLoginSuccess) melalui StateFlow.
// Menjalankan Tugas Menerima permintaan login(email, password) dari LoginScreen dan meneruskannya ke AuthRepository.

package com.example.nawasena.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nawasena.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// --- 1. DEFINISI STATE ---

// Data Class yang merepresentasikan status UI (LoginScreen) saat ini
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String? = null
)

// --- 2. KELAS UTAMA VIEWMODEL ---

class AuthViewModel() : ViewModel() {
    private val authRepository: AuthRepository = AuthRepository()

    // MutableStateFlow untuk menyimpan status dan mengizinkan ViewModel mengubahnya
    private val _uiState = MutableStateFlow(AuthUiState())

    // StateFlow yang diekspos ke Composable. UI hanya bisa MEMBACA dari sini.
    val uiState: StateFlow<AuthUiState> = _uiState

    // Fungsi yang dipanggil dari LoginScreen (ketika tombol 'Masuk' ditekan)
    fun login(email: String, password: String) {
        // Hanya jalankan jika aplikasi tidak dalam status loading
        if (_uiState.value.isLoading) return

        // Gunakan coroutine scope untuk menjalankan operasi background (API Call)
        viewModelScope.launch {
            // 1. Set Status Loading
            _uiState.value = AuthUiState(isLoading = true)

            try {
                // 2. Panggil Repository untuk proses Autentikasi
                val success = authRepository.login(email, password) // Asumsi AuthRepository punya fungsi login

                if (success) {
                    // 3. Login Sukses
                    _uiState.value = AuthUiState(isLoginSuccess = true)
                } else {
                    // 4. Login Gagal (dari response Repository, misalnya status 401)
                    _uiState.value = AuthUiState(errorMessage = "Email atau Kata Sandi salah.")
                }
            } catch (e: Exception) {
                // 5. Kegagalan Jaringan/Exception
                _uiState.value = AuthUiState(errorMessage = "Gagal terhubung ke server: ${e.message}")
            }
        }
    }
}