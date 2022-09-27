package com.example.ringer_app.repository

import androidx.lifecycle.LiveData
import com.example.ringer_app.database.ProfileDao
import com.example.ringer_app.repository.model.UserProfile

class ProfileRepository(private val profileDao: ProfileDao) {
    val getProfiles: LiveData<List<UserProfile>> = profileDao.getProfiles()

    suspend fun addProfile(userProfile: UserProfile) {
        profileDao.addProfile(userProfile)
    }

    suspend fun updateProfile(userProfile: UserProfile) {
        profileDao.updateProfile(userProfile)
    }

    suspend fun deleteProfile(userProfile: UserProfile) {
        profileDao.deleteProfile(userProfile)
    }
}