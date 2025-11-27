package com.example.nawasena.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nawasena.data.model.User
import com.example.nawasena.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- 1. DEFINISI STATE (Diperbarui) ---
// Kita ganti 'isLoginSuccess' dengan 'currentUser'.
// Jika currentUser != null, artinya login sukses.
data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val errorMessage: String? = null
)

// --- 2. KELAS UTAMA VIEWMODEL ---

class AuthViewModel(
    private val repository: AuthRepository // DI: Terima Repository dari luar
) : ViewModel() {

    // Menggunakan asStateFlow() agar lebih aman (Read-only di luar)
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Cek apakah user sudah login sebelumnya (saat aplikasi baru dibuka)
    init {
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        if (repository.isUserLoggedIn()) {
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                // Mencoba ambil data profil user terbaru
                // (Kamu bisa sesuaikan logic ini di repository jika mau)
                val uid = repository.getCurrentUserUid()
                if (uid != null) {
                    // Login ulang diam-diam (mocking user object or fetch real data)
                    // Agar simpel, kita anggap user ada dulu, nanti UI akan fetch data
                    // Atau idealnya repository punya fungsi 'getCurrentUserData'
                }
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Fungsi Login
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            // 1. Set Loading
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 2. Panggil Repository
            val result = repository.login(email, pass)

            // 3. Update State berdasarkan hasil
            result.onSuccess { user ->
                _uiState.update {
                    it.copy(isLoading = false, currentUser = user, errorMessage = null)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.message ?: "Login Gagal")
                }
            }
        }
    }

    // Fungsi Register (Wajib ada karena dipakai di RegisterScreen)
    fun register(name: String, email: String, pass: String, phone: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.register(name, email, pass, phone)

            result.onSuccess { user ->
                _uiState.update {
                    it.copy(isLoading = false, currentUser = user, errorMessage = null)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.message ?: "Registrasi Gagal")
                }
            }
        }
    }

    // Fungsi Logout
    fun logout() {
        repository.logout()
        _uiState.update { AuthUiState() } // Reset ke state awal
    }

    // Membersihkan pesan error setelah ditampilkan (opsional)
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}