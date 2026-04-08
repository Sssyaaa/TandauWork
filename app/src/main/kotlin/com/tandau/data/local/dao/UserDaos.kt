package com.tandau.data.local.dao

import androidx.room.*
import com.tandau.data.local.entities.*

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile): Long

    @Query("SELECT * FROM user_profile WHERE id = :userId")
    suspend fun getUserProfile(userId: Long): UserProfile?

    @Query("SELECT * FROM user_profile WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserProfile?
}

@Dao
interface UserSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: UserSession)

    @Query("SELECT * FROM user_session WHERE id = 0")
    suspend fun getSession(): UserSession?

    @Query("DELETE FROM user_session")
    suspend fun clearSession()
}

@Dao
interface InterestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterests(interests: List<Interest>)

    @Query("SELECT * FROM interests")
    suspend fun getAllInterests(): List<Interest>
}

@Dao
interface StrengthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStrengths(strengths: List<Strength>)

    @Query("SELECT * FROM strengths")
    suspend fun getAllStrengths(): List<Strength>
}

@Dao
interface UserInterestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInterests(userInterests: List<UserInterest>)

    @Query("SELECT * FROM user_interests WHERE userId = :userId")
    suspend fun getUserInterests(userId: Long): List<UserInterest>
}

@Dao
interface UserStrengthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStrengths(userStrengths: List<UserStrength>)

    @Query("SELECT * FROM user_strengths WHERE userId = :userId")
    suspend fun getUserStrengths(userId: Long): List<UserStrength>
}
