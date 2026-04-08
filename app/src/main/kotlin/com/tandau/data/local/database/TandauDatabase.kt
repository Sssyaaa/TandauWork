package com.tandau.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tandau.data.local.dao.*
import com.tandau.data.local.entities.*

@Database(entities = [UserProfile::class, UserSession::class, Interest::class, Strength::class, UserInterest::class, UserStrength::class], version = 2, exportSchema = false)
abstract class TandauDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun userSessionDao(): UserSessionDao
    abstract fun interestDao(): InterestDao
    abstract fun strengthDao(): StrengthDao
    abstract fun userInterestDao(): UserInterestDao
    abstract fun userStrengthDao(): UserStrengthDao

    companion object {
        @Volatile
        private var INSTANCE: TandauDatabase? = null

        fun getDatabase(context: Context): TandauDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TandauDatabase::class.java,
                    "tandau_database"
                )
                .fallbackToDestructiveMigration() // Simple for MVP/Refactor
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
