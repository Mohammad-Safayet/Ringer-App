package com.example.ringer_app.repository.model

data class Profile (
    val id: Int,
    val name: String,
    val ringerMode: String,
    val startTime: String,
    val stopTime: String,
    val location: Address,
    val radius: Double,
    val isActive: Boolean
)