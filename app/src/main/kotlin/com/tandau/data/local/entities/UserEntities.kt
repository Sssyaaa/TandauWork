package com.tandau.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val password: String, // Added password
    val username: String,
    val city: String, // Added city
    val age: Int
)

@Entity(tableName = "user_session")
data class UserSession(
    @PrimaryKey val id: Int = 0, // Only one session at a time
    val userId: Long,
    val userEmail: String
)

@Entity(tableName = "interests")
data class Interest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

@Entity(tableName = "strengths")
data class Strength(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

@Entity(tableName = "user_interests")
data class UserInterest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val interestId: Long
)

@Entity(tableName = "user_strengths")
data class UserStrength(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val strengthId: Long
)
