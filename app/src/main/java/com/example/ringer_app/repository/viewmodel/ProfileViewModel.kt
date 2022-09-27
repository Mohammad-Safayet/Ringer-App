package com.example.ringer_app.repository.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.ringer_app.database.ProfileDatabase
import com.example.ringer_app.repository.model.UserProfile
import com.example.ringer_app.repository.ProfileRepository
import com.example.ringer_app.utils.Utils
import com.example.ringer_app.repository.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = ProfileViewModel::class.java.toString()

    private val repository: ProfileRepository
    val profiles: LiveData<List<Profile>>

    init {
        val profileDao = ProfileDatabase.getDatabase(application).getProfileDao()
        repository = ProfileRepository(profileDao)

        profiles = repository.getProfiles.map {
            val profiles = it.map { userProfile: UserProfile ->
                return@map Profile(
                    userProfile.id,
                    userProfile.title,
                    userProfile.ringerMode,
                    userProfile.startTime,
                    userProfile.stopTime,
                    Utils.getAddress(application, userProfile.latitude, userProfile.longitude),
                    userProfile.radius,
                    userProfile.isActive
                )
            }

            return@map profiles
        }
    }

    fun addProfile(profile: Profile) {
        viewModelScope.launch(Dispatchers.IO) {
            val userProfile = UserProfile(
                0,
                profile.name,
                profile.ringerMode,
                profile.startTime,
                profile.stopTime,
                profile.location.address,
                profile.location.latitude,
                profile.location.longitude,
                profile.radius,
                profile.isActive
            )

            repository.addProfile(userProfile)

        }
    }

    fun updateProfile(profile: Profile) {
        viewModelScope.launch(Dispatchers.IO) {
            val userProfile = UserProfile(
                profile.id,
                profile.name,
                profile.ringerMode,
                profile.startTime,
                profile.stopTime,
                profile.location.address,
                profile.location.latitude,
                profile.location.longitude,
                profile.radius,
                profile.isActive
            )

            repository.updateProfile(userProfile)

        }
    }

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch(Dispatchers.IO) {
            val userProfile = UserProfile(
                profile.id,
                profile.name,
                profile.ringerMode,
                profile.startTime,
                profile.stopTime,
                profile.location.address,
                profile.location.latitude,
                profile.location.longitude,
                profile.radius,
                profile.isActive
            )

            repository.deleteProfile(userProfile)
        }
    }

    fun printLog(): Unit {
        Log.d(TAG, "printLog: ")
    }
}