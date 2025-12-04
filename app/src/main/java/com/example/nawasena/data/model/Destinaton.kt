package com.example.nawasena.data.model

import com.google.firebase.firestore.PropertyName

data class Destination(
    var id: String = "",
    val name: String = "",

    @PropertyName("location_text")
    val location: String = "",

    @PropertyName("image_url")
    val imageUrl: String = "",

    val rating: Double = 0.0,
    @PropertyName("review_count")
    val reviewCount: Int = 0,

    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    // --- TAMBAHAN KEYWORDS ---
    // Di Firestore, Array of String otomatis dikonversi jadi List<String> di Kotlin
    val keywords: List<String> = emptyList()
)