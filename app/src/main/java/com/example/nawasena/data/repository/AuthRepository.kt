// Tugas utamanya saat ini adalah mensimulasikan panggilan ke server (fungsi suspend fun login(...)).

package com.example.nawasena.data.repository

import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class AuthRepository {

    // Inisialisasi Firebase Auth instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): Boolean {
        return try {
            // Panggil API Firebase secara asynchronous dan tunggu hasilnya (await)
            val result = auth.signInWithEmailAndPassword(email, password).await()

            // Jika result tidak null (berarti berhasil), kembalikan true
            result.user != null

        } catch (e: FirebaseAuthException) {
            // Tangkap exception spesifik Firebase (misalnya, password salah, user tidak ditemukan)
            false // gagal
        } catch (e: Exception) {
            // Tangkap exception umum (misalnya, masalah jaringan)
            false // gagal
        }
    }
}