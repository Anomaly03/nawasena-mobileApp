package com.example.nawasena.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    // Ambil data user berdasarkan UID secara Realtime (Flow)
    @Query("SELECT * FROM user_profile WHERE uid = :uid")
    fun getUserProfile(uid: String): Flow<UserProfile?>

    // Insert atau Update data
    // Kita pakai OnConflictStrategy.REPLACE agar kalau data ada, ditimpa (update)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(userProfile: UserProfile)

    // Query khusus untuk update field parsial (jika ingin lebih rapi)
    @Query("UPDATE user_profile SET name = :name, email = :email WHERE uid = :uid")
    suspend fun updateFirebaseInfo(uid: String, name: String, email: String)
}