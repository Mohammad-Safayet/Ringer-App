package com.example.ringer_app.service

import android.content.Context
import android.content.Intent
import com.example.ringer_app.utils.Constants


class ServiceHelper(private val mContext: Context) {

    fun startLocationService() {
        val intent = Intent(mContext, RingerAppService::class.java).apply {
            action = Constants.ACTION_START_LOCATION_SERVICE
        }
        mContext.startForegroundService(intent)
    }

    fun stopLocationService() {
        val intent = Intent(mContext, RingerAppService::class.java).apply {
            action = Constants.ACTION_STOP_LOCATION_SERVICE
        }
        mContext.stopService(intent)
    }
}