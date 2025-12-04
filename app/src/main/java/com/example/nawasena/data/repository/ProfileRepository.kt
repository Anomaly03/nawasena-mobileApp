package com.example.nawasena.data.repository

import android.util.Log
import com.example.nawasena.data.local.ProfileDao
import com.example.nawasena.data.local.UserProfile
import com.example.nawasena.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val profileDao: ProfileDao,
    private val firestore: FirebaseFirestore
) {

    // 1. Ambil data Realtime dari Room untuk UI
    fun getLocalProfile(uid: String): Flow<UserProfile?> {
        return profileDao.getUserProfile(uid)
    }

    // 2. Sinkronisasi: Download dari Cloud -> Simpan ke Room
    suspend fun syncProfileFromCloud(firebaseUser: User) {
        try {
            val snapshot = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (snapshot.exists()) {
                // Data ada di Cloud -> Update Room
                val cloudProfile = UserProfile(
                    uid = firebaseUser.uid,
                    name = snapshot.getString("name") ?: firebaseUser.name,
                    email = snapshot.getString("email") ?: firebaseUser.email,
                    phoneNumber = snapshot.getString("phoneNumber") ?: "",
                    birthDate = snapshot.getString("birthDate") ?: "",
                    username = snapshot.getString("username") ?: ""
                )
                profileDao.insertOrUpdate(cloudProfile)
            } else {
                // User baru (belum ada di Cloud) -> Pastikan data default ada di Room
                // Kita cek manual lewat query sederhana atau try-catch,
                // tapi karena kita pakai insertOrUpdate di DAO, kita bisa langsung insert default
                // HANYA JIKA kita yakin ini user baru bersih.
                // Agar aman, kita bisa insert default yg minim field.

                // Disini kita biarkan saja, nanti UI akan menampilkan default dari UserFirebase
                // Atau kita insert inisial data:
                val initialProfile = UserProfile(
                    uid = firebaseUser.uid,
                    name = firebaseUser.name,
                    email = firebaseUser.email
                )
                // Gunakan strategy conflict IGNORE di DAO untuk ini sebenarnya lebih aman,
                // tapi insertOrUpdate oke asalkan field lain defaultnya kosong.
                profileDao.insertOrUpdate(initialProfile)
            }
        } catch (e: Exception) {
            Log.e("ProfileRepo", "Gagal sync dari Firestore: ${e.message}")
        }
    }

    // 3. Update Data: Simpan ke Room DAN Upload ke Cloud
    suspend fun updateProfile(userProfile: UserProfile) {
        // A. Update Lokal (Cepat)
        profileDao.insertOrUpdate(userProfile)

        // B. Update Cloud (Background)
        val dataMap = hashMapOf(
            "uid" to userProfile.uid,
            "name" to userProfile.name,
            "email" to userProfile.email,
            "username" to userProfile.username,
            "phoneNumber" to userProfile.phoneNumber,
            "birthDate" to userProfile.birthDate
        )

        try {
            firestore.collection("users")
                .document(userProfile.uid)
                .set(dataMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e("ProfileRepo", "Gagal upload ke Firestore: ${e.message}")
        }
    }
}