package com.example.nawasena.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserProfile::class], version = 1, exportSchema = false)
abstract class NawasenaDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: NawasenaDatabase? = null

        fun getDatabase(context: Context): NawasenaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NawasenaDatabase::class.java,
                    "nawasena_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}