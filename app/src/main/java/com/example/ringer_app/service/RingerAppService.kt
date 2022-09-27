package com.example.ringer_app.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.AudioManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.ringer_app.database.ProfileDao
import com.example.ringer_app.database.ProfileDatabase
import com.example.ringer_app.repository.model.Address
import com.example.ringer_app.repository.model.Profile
import com.example.ringer_app.repository.model.UserProfile
import com.example.ringer_app.utils.ConnectionLiveData
import com.example.ringer_app.utils.ConnectivityObserver
import com.example.ringer_app.utils.Constants
import com.example.ringer_app.utils.Utils
import com.example.ringer_app.utils.Utils.formatEqn3
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import java.time.LocalTime

class RingerAppService : Service() {

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback

    // User Profile
    private lateinit var mProfileDao: ProfileDao
    private val mActiveProfiles: MutableList<UserProfile> = mutableListOf()

    private var mCurrentLocation: Location? = null
    private var mIntent: Intent = Intent()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            if (action != null) {
                if (action == Constants.ACTION_START_LOCATION_SERVICE) {
                    val status = ConnectionLiveData(this).observe().asLiveData()
                    start(status.value ?: ConnectivityObserver.Status.Unavailable)
                } else if (action == Constants.ACTION_STOP_LOCATION_SERVICE) {
                    stop()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    private fun start(networkStatus: ConnectivityObserver.Status) {

        mProfileDao = ProfileDatabase.getDatabase(applicationContext).getProfileDao()

//        if (networkStatus == ConnectivityObserver.Status.Unavailable
//            || networkStatus == ConnectivityObserver.Status.Lost
//        ) {
//            Log.d(TAG, "start: $networkStatus")
//            mLocationManager = (this.getSystemService(LOCATION_SERVICE) as LocationManager)
//
//            val locationListener = LocationListener {
//                locationResult(it)
//            }
//
//            mLocationManager.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER,
//                1000 * 10 * 1,
//                5.0f,
//                locationListener
//            )
//            Log.d("Network", "Network")
//        } else {
        mFusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(applicationContext)

        mLocationRequest = LocationRequest.create().apply {
            interval = (1000 * INTERVAL)
            fastestInterval = (1000 * FAST)
            maxWaitTime = (1000 * MAX)
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locaiton: LocationResult) {
                super.onLocationResult(locaiton)
                locaiton.lastLocation?.let { locationResult(it) }
            }
        }

        mFusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.getMainLooper()
        )
//        }

        startForeground(
            Constants.LOCATION_SERVICE_ID,
            Utils.generateNotification(applicationContext)
        )
    }

    private fun stop() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
        stopForeground(true)
        stopSelf()
    }

    private fun changeRingerMode(context: Context, profile: UserProfile) {
        val currentTime = mCurrentLocation?.time
        val time = formatEqn3.format(currentTime)
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val targetLocation = Location("")
        targetLocation.latitude = profile.latitude
        targetLocation.longitude = profile.longitude

        val isInRadius = mCurrentLocation!!.distanceTo(targetLocation) <= profile.radius

        // Checks the local time is in the profile time
        val now = LocalTime.parse(time)
        val startTime = LocalTime.parse(profile.startTime).isBefore(now)
        val stopTime = LocalTime.parse(profile.stopTime).isAfter(now)

        if (startTime && stopTime && isInRadius) {
            Log.d(
                TAG,
                "changeRingerMode: ${profile.ringerMode == "Silent"}"
            )
            if (audioManager.ringerMode != AudioManager.RINGER_MODE_VIBRATE && profile.ringerMode == "Vibrate") {
                audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                Toast.makeText(
                    this,
                    "Phone is put on vibrate mode.",
                    Toast.LENGTH_LONG
                ).show()
            } else if (audioManager.ringerMode != AudioManager.RINGER_MODE_SILENT && profile.ringerMode == "Silent") {
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                Toast.makeText(
                    this,
                    "Phone is put on silent mode.",
                    Toast.LENGTH_LONG
                ).show()
            } else if (audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL && profile.ringerMode == "Ring") {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                Toast.makeText(
                    this,
                    "Phone is put on ringer mode.",
                    Toast.LENGTH_LONG
                ).show()
            }

            Log.d(
                TAG,
                "start: $profile $startTime $stopTime $isInRadius ${profile.ringerMode} ${audioManager.ringerMode}"
            )
        }
    }

    // Fetch user profile from database and set the array field
    private fun fetchUserProfile() {
        mProfileDao.getProfiles().observeForever {
            for (profile in it) {
                if (profile.isActive) {
                    changeRingerMode(applicationContext, profile)
                }
            }
        }
    }

    private fun locationResult(location: Location) {
        mCurrentLocation = location

        fetchUserProfile()

        Log.d(
            TAG,
            "onLocationResult: ${mCurrentLocation!!.latitude} ${mCurrentLocation!!.longitude}"
        )

        mIntent.action = LOCATION_TRIGGER
        mIntent.putExtra(LATITUDE, mCurrentLocation!!.latitude)
        mIntent.putExtra(LONGITUDE, mCurrentLocation!!.longitude)
        sendBroadcast(mIntent)
    }

    companion object {
        val TAG = RingerAppService::class.java.toString()
        const val SHORT_CLASSNAME = ".service.RingerAppService"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val LOCATION_TRIGGER = "locationTrigger"
        const val INTERVAL = 10L
        const val FAST = 5L
        const val MAX = 60L
        val address: MutableLiveData<Address> = MutableLiveData<Address>()
    }
}