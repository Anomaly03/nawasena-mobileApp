package com.example.nawasena.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val uid: String, // UID dari Firebase jadi Primary Key
    val name: String,
    val email: String,

    // Field Tambahan (Lokal)
    val username: String = "",
    val phoneNumber: String = "",
    val birthDate: String = ""
)