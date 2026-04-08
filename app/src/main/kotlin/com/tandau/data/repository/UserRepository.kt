package com.tandau.data.repository

import com.tandau.data.local.database.TandauDatabase
import com.tandau.data.local.entities.*

class UserRepository(private val database: TandauDatabase) {

    private val userProfileDao = database.userProfileDao()
    private val userSessionDao = database.userSessionDao()
    private val interestDao = database.interestDao()
    private val strengthDao = database.strengthDao()
    private val userInterestDao = database.userInterestDao()
    private val userStrengthDao = database.userStrengthDao()

    suspend fun saveUserProfile(userProfile: UserProfile): Long {
        return userProfileDao.insertUserProfile(userProfile)
    }

    suspend fun getUserProfile(userId: Long): UserProfile? {
        return userProfileDao.getUserProfile(userId)
    }

    suspend fun getUserByEmail(email: String): UserProfile? {
        return userProfileDao.getUserByEmail(email)
    }

    suspend fun createSession(userId: Long, email: String) {
        userSessionDao.insertSession(UserSession(userId = userId, userEmail = email))
    }

    suspend fun getSession(): UserSession? {
        return userSessionDao.getSession()
    }

    suspend fun clearSession() {
        userSessionDao.clearSession()
    }

    suspend fun saveInterests(interests: List<Interest>) {
        interestDao.insertInterests(interests)
    }

    suspend fun getAllInterests(): List<Interest> {
        return interestDao.getAllInterests()
    }

    suspend fun saveUserInterests(userInterests: List<UserInterest>) {
        userInterestDao.insertUserInterests(userInterests)
    }

    suspend fun getUserInterests(userId: Long): List<UserInterest> {
        return userInterestDao.getUserInterests(userId)
    }

    suspend fun saveStrengths(strengths: List<Strength>) {
        strengthDao.insertStrengths(strengths)
    }

    suspend fun getAllStrengths(): List<Strength> {
        return strengthDao.getAllStrengths()
    }

    suspend fun saveUserStrengths(userStrengths: List<UserStrength>) {
        userStrengthDao.insertUserStrengths(userStrengths)
    }

    suspend fun getUserStrengths(userId: Long): List<UserStrength> {
        return userStrengthDao.getUserStrengths(userId)
    }
}
