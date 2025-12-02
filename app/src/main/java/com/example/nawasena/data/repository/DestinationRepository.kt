package com.example.nawasena.data.repository

import android.util.Log
import com.example.nawasena.data.model.Destination
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DestinationRepository(
    private val firestore: FirebaseFirestore
) {
    // Fungsi untuk mengambil SEMUA data destinasi
    suspend fun getAllDestinations(): Result<List<Destination>> {
        return try {
            val snapshot = firestore.collection("destinations").get().await()

            val destinations = snapshot.documents.map { doc ->
                // --- MANUAL MAPPING (Jurus Anti Error) ---
                // Kita ambil data mentah satu per satu secara manual

                // 1. Ambil Nama
                val name = doc.getString("name") ?: ""

                // 2. Ambil Lokasi (Cek 2 kemungkinan nama field biar aman)
                val location = doc.getString("location_text")
                    ?: doc.getString("location")
                    ?: "Lokasi Tidak Ditemukan"

                // 3. Ambil Gambar (Cek url atau nama file)
                val imageUrl = doc.getString("image_url")
                    ?: doc.getString("image_name")
                    ?: ""

                // 4. Ambil Angka (Rating & Review)
                val rating = doc.getDouble("rating") ?: 0.0
                // Firestore simpan angka bulat sbg Long, kita butuh Int
                val reviewCount = doc.getLong("review_count")?.toInt()
                    ?: doc.getLong("reviewCount")?.toInt()
                    ?: 0

                // 5. Ambil Koordinat
                val latitude = doc.getDouble("latitude") ?: 0.0
                val longitude = doc.getDouble("longitude") ?: 0.0

                // 6. Ambil Keywords (List)
                val keywords = (doc.get("keywords") as? List<String>) ?: emptyList()

                // --- RAKIT ULANG KE DALAM OBJECT DESTINATION ---
                Destination(
                    id = doc.id, // ID Dokumen Firestore
                    name = name,
                    location = location,
                    imageUrl = imageUrl,
                    rating = rating,
                    reviewCount = reviewCount,
                    latitude = latitude,
                    longitude = longitude,
                    keywords = keywords
                )
            }

            // LOGGING: Cek di Logcat (bagian bawah Android Studio)
            // Cari kata kunci: "FirestoreCheck"
            Log.d("FirestoreCheck", "Total Data: ${destinations.size}")
            destinations.forEach {
                Log.d("FirestoreCheck", "Data Loaded -> Nama: ${it.name}, Lokasi: ${it.location}, Img: ${it.imageUrl}")
            }

            Result.success(destinations)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("FirestoreCheck", "Error mengambil data: ${e.message}")
            Result.failure(e)
        }
    }
}