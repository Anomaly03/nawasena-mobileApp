// Tugas utamanya saat ini adalah mensimulasikan panggilan ke server (fungsi suspend fun login(...)).

package com.example.nawasena.data.repository

import kotlinx.coroutines.delay

class AuthRepository()
{
    suspend fun login(email: String, password: String): Boolean {
        // --- SIMULASI API CALL ---

        // 1. Simulasikan latency jaringan selama 1 detik.
        delay(1000)

        // 2. Simulasikan logika verifikasi server:
        // Cek apakah email dan password cocok dengan data dummy (test@nawasena.com, nawasena)
        return email == "test@nawasena.com" && password == "nawasena"

        // Dalam implementasi nyata, ini akan terlihat seperti:
        // val response = apiService.login(email, password)
        // return response.isSuccessful
    }
}