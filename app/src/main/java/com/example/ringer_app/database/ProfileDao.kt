package com.example.ringer_app.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.ringer_app.repository.model.UserProfile

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addProfile(userProfile: UserProfile)

    @Update
    suspend fun updateProfile(userProfile: UserProfile)

    @Delete
    suspend fun deleteProfile(userProfile: UserProfile)

    @Query("SELECT * FROM user_profile ORDER BY id ASC")
    fun getProfiles(): LiveData<List<UserProfile>>
}