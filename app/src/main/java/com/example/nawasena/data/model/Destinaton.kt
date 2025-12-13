// File: com/example/nawasena/data/model/Destination.kt
package com.example.nawasena.data.model

import com.google.firebase.firestore.PropertyName

data class Destination(
    var id: String = "",
    val name: String = "",

    @PropertyName("location_text")
    val location: String = "",

    @PropertyName("image_url")
    val imageUrl: String = "",

    // --- UPDATE ---
    val description: String = "Belum ada deskripsi.",
    val viewCount: Int = 0,
    // --------------

    val rating: Double = 0.0,
    @PropertyName("review_count")
    val reviewCount: Int = 0,

    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val keywords: List<String> = emptyList()
)


data class Review(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)