package com.example.ringer_app.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ringer_app.utils.Constants.PROFILE_ENTITY_NAME

@Entity(tableName = PROFILE_ENTITY_NAME)
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val ringerMode: String,
    val startTime: String,
    val stopTime: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
    val isActive: Boolean,
)