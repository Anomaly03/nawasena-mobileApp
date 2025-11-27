package com.example.nawasena.data.repository

import com.example.nawasena.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    // --- LOGIKA FIREBASE ASLI ---

    // 1. Fungsi Login:
    // Mengecek Email & Password ke Firebase Auth, lalu mengambil data profil (Nama, HP) dari Firestore
    suspend fun login(email: String, pass: String): Result<User> {
        return try {
            // A. Verifikasi Login ke Server Firebase (Bukan simulasi delay)
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("User ID error")

            // B. Ambil data tambahan (Username/Nama) dari Firestore
            val snapshot = firestore.collection("users").document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
                ?: throw Exception("Data user tidak ditemukan di database")

            Result.success(user)
        } catch (e: Exception) {
            // Jika password salah atau user tidak ada, error akan ditangkap di sini
            Result.failure(e)
        }
    }

    // 2. Fungsi Register:
    // Membuat akun baru di Auth, dan menyimpan biodata di Firestore
    suspend fun register(name: String, email: String, pass: String, phone: String): Result<User> {
        return try {
            // A. Buat user baru di Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("Gagal membuat user")

            // B. Siapkan data profil
            val newUser = User(
                uid = uid,
                name = name,
                email = email,

            )

            // C. Simpan data profil ke Firestore (Koleksi 'users')
            firestore.collection("users").document(uid).set(newUser).await()

            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. Fungsi Logout
    fun logout() {
        auth.signOut()
    }

    // 4. Cek apakah user masih login (agar tidak perlu login ulang saat buka aplikasi)
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }
}