package com.example.nawasena.data.repository

import android.util.Log
import com.example.nawasena.data.model.Destination
import com.example.nawasena.data.model.Review
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class DestinationRepository(
    private val firestore: FirebaseFirestore
) {

    // --- HELPER: Mapping Dokumen ke Object Destination ---
    private fun mapDocumentToDestination(doc: com.google.firebase.firestore.DocumentSnapshot): Destination {
        val name = doc.getString("name") ?: ""

        val location = doc.getString("location_text")
            ?: doc.getString("location")
            ?: "Lokasi Tidak Ditemukan"

        val imageUrl = doc.getString("image_url")
            ?: doc.getString("image_name")
            ?: ""

        val description = doc.getString("description")
            ?: doc.getString("desc")
            ?: "Belum ada deskripsi."

        val rating = doc.getDouble("rating") ?: 0.0

        // [LOGIC PINTAR] Ambil review count yang VALID (Bukan 0)
        // Jika review_count ada isinya (>0), pakai itu. Jika tidak, cek reviewCount.
        val countSnake = doc.getLong("review_count")
        val countCamel = doc.getLong("reviewCount")

        val reviewCount = if (countSnake != null && countSnake > 0) countSnake.toInt()
        else if (countCamel != null && countCamel > 0) countCamel.toInt()
        else 0

        val viewCount = doc.getLong("viewCount")?.toInt() ?: 0

        val latitude = doc.getDouble("latitude") ?: 0.0
        val longitude = doc.getDouble("longitude") ?: 0.0
        val keywords = (doc.get("keywords") as? List<String>) ?: emptyList()

        return Destination(
            id = doc.id,
            name = name,
            location = location,
            imageUrl = imageUrl,
            description = description,
            viewCount = viewCount,
            rating = rating,
            reviewCount = reviewCount,
            latitude = latitude,
            longitude = longitude,
            keywords = keywords
        )
    }

    suspend fun getAllDestinations(): Result<List<Destination>> {
        return try {
            val snapshot = firestore.collection("destinations").get().await()
            val destinations = snapshot.documents.map { mapDocumentToDestination(it) }
            Result.success(destinations)
        } catch (e: Exception) {
            Log.e("Repository", "Error getAllDestinations: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getDestinationById(id: String): Result<Destination> {
        return try {
            val doc = firestore.collection("destinations").document(id).get().await()

            // Increment View Count
            firestore.collection("destinations").document(id).update("viewCount", FieldValue.increment(1))

            // Mapping manual
            val data = mapDocumentToDestination(doc)
            Result.success(data)
        } catch (e: Exception) {
            Log.e("Repository", "Error getDestinationById: ${e.message}")
            Result.failure(e)
        }
    }

    // --- FIX UTAMA ADA DI SINI ---
    suspend fun addReview(destinationId: String, review: Review): Result<Boolean> {
        return try {
            Log.d("Repository", "Mulai proses addReview untuk ID: $destinationId")

            val destRef = firestore.collection("destinations").document(destinationId)
            val reviewRef = destRef.collection("reviews").document()

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(destRef)

                // 1. Ambil Data Lama (Handle Null Safety)
                val oldRating = snapshot.getDouble("rating") ?: 0.0

                // 2. LOGIC PINTAR: Pilih count yang masuk akal
                val countSnake = snapshot.getLong("review_count") ?: 0L
                val countCamel = snapshot.getLong("reviewCount") ?: 0L

                // Prioritas: Jika snake_case > 0 pakai itu. Jika tidak, pakai camelCase.
                val oldReviewCount = if (countSnake > 0) countSnake else countCamel

                Log.d("Repository", "Data Lama -> Rating: $oldRating, Count: $oldReviewCount")

                // 3. RUMUS UPDATE (Pakai Double agar akurat)
                val newReviewCount = oldReviewCount + 1
                // Rumus: ((Rata2 Lama * Jumlah Lama) + Rating Baru) / Jumlah Baru
                val totalScore = (oldRating * oldReviewCount) + review.rating.toDouble()
                val newAverageRating = totalScore / newReviewCount

                Log.d("Repository", "Data Baru -> Rating: $newAverageRating, Count: $newReviewCount")

                // 4. Update ke Firestore (Pakai format snake_case standar)
                transaction.update(destRef, "rating", newAverageRating)
                transaction.update(destRef, "review_count", newReviewCount)

                // (Opsional) Hapus field duplikat biar rapi, tapi komen dulu biar aman
                // transaction.update(destRef, "reviewCount", FieldValue.delete())

                // 5. Simpan Review User
                transaction.set(reviewRef, review)
            }.await()

            Log.d("Repository", "Transaction Berhasil!")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("Repository", "Gagal addReview: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

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
            Log.e("Repository", "Error getReviews: ${e.message}")
            Result.failure(e)
        }
    }
}