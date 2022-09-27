package com.example.ringer_app.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.ringer_app.service.RingerAppService
import com.example.ringer_app.repository.viewmodel.LocationViewModel

class LocationBroadcastReceiver(private val locationViewModel: LocationViewModel) :
    BroadcastReceiver() {
    private val TAG = "RingerBroadcastReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        val longitude = intent!!.getDoubleExtra(RingerAppService.LONGITUDE, 0.0)
        val latitude = intent.getDoubleExtra(RingerAppService.LATITUDE, 0.0)

        Log.d(TAG, "onReceive: longitude $longitude latitude $latitude")

        context?.let { Utils.getAddress(it, latitude, longitude) }
            ?.let { locationViewModel.setAddress(it) }
    }
}