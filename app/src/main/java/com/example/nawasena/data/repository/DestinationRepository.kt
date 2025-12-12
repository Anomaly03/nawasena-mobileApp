package com.example.nawasena.data.repository

import android.util.Log
import com.example.nawasena.data.model.Destination
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.nawasena.data.model.Review
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query

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
    // 1. Ambil Detail Destinasi by ID
    suspend fun getDestinationById(id: String): Result<Destination> {
        return try {
            val doc = firestore.collection("destinations").document(id).get().await()

            // Increment View Count (Setiap kali dibuka, view nambah 1)
            // Fire-and-forget (tidak perlu nunggu result)
            firestore.collection("destinations").document(id).update("viewCount", FieldValue.increment(1))

            // Mapping manual seperti sebelumnya (copy logika mapping dari getAllDestinations)
            // Atau untuk singkatnya anggap mapping sudah terjadi di sini:
            val data = doc.toObject(Destination::class.java)?.copy(id = doc.id)
            // Note: Pastikan field di Firestore sesuai atau pakai mapping manual jika nama field beda

            if (data != null) Result.success(data) else Result.failure(Exception("Data kosong"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. Tambah Review (Logic Rumit: Update Rating Rata-rata)
    suspend fun addReview(destinationId: String, review: Review): Result<Boolean> {
        return try {
            val destRef = firestore.collection("destinations").document(destinationId)
            val reviewRef = destRef.collection("reviews").document()

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(destRef)

                val oldRating = snapshot.getDouble("rating") ?: 0.0
                val oldReviewCount = snapshot.getLong("review_count") ?: 0L

                // --- RUMUS RATA-RATA BINTANG ---
                val newReviewCount = oldReviewCount + 1
                val newAverageRating = ((oldRating * oldReviewCount) + review.rating) / newReviewCount

                // Update Destinasi
                transaction.update(destRef, "rating", newAverageRating)
                transaction.update(destRef, "review_count", newReviewCount)

                // Simpan Review
                transaction.set(reviewRef, review)
            }.await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // 3. Ambil List Review
    suspend fun getReviews(destinationId: String): Result<List<Review>> {
        return try {
            val snapshot = firestore.collection("destinations")
                .document(destinationId)
                .collection("reviews")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val reviews = snapshot.toObjects(Review::class.java)
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}