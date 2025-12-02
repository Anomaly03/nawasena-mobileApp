package com.example.nawasena.utils

import com.example.nawasena.data.model.Destination
import com.google.firebase.firestore.FirebaseFirestore

object DestinationSeeder {

    // Data Dummy Lengkap dengan Koordinat & Keyword
    val dummyDestinations = listOf(
        Destination(
            name = "Museum Mpu Tantular",
            location = "Buduran, Sidoarjo",
            imageUrl = "", // Nanti isi link gambar asli
            rating = 4.3,
            reviewCount = 400,
            latitude = -7.430847,
            longitude = 112.719603,
            keywords = listOf("sejarah", "museum", "edukasi", "sidoarjo", "kuno", "budaya")
        ),
        Destination(
            name = "Lumpur Lapindo (Sidoarjo Mud)",
            location = "Porong, Sidoarjo",
            imageUrl = "",
            rating = 4.6,
            reviewCount = 3412,
            latitude = -7.525774,
            longitude = 112.710777,
            keywords = listOf("bencana", "alam", "lumpur", "unik", "sidoarjo", "wisata")
        ),
        Destination(
            name = "Gunung Bromo",
            location = "Probolinggo, Jawa Timur",
            imageUrl = "",
            rating = 4.9,
            reviewCount = 5400,
            latitude = -7.942493,
            longitude = 112.953012,
            keywords = listOf("gunung", "hiking", "sunrise", "dingin", "jawa timur", "mendaki", "alam")
        ),
        Destination(
            name = "Air Terjun Madakaripura",
            location = "Lumbang, Probolinggo",
            imageUrl = "",
            rating = 4.7,
            reviewCount = 1200,
            latitude = -7.854866,
            longitude = 113.003926,
            keywords = listOf("air terjun", "basah", "alam", "segar", "probolinggo")
        ),
        Destination(
            name = "Pantai Kenjeran",
            location = "Surabaya",
            imageUrl = "",
            rating = 4.0,
            reviewCount = 2000,
            latitude = -7.250445,
            longitude = 112.798950,
            keywords = listOf("pantai", "laut", "pasir", "surabaya", "taman")
        )
    )

    // Fungsi untuk Upload
    fun uploadData(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val batch = firestore.batch() // Batch agar sekali kirim langsung banyak

        dummyDestinations.forEach { data ->
            // Buat dokumen baru di collection 'destinations'
            val docRef = firestore.collection("destinations").document()
            batch.set(docRef, data)
        }

        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}