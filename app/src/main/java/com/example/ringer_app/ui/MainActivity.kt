package com.example.ringer_app.ui

import android.Manifest
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ringer_app.databinding.ActivityMainBinding
import com.example.ringer_app.service.RingerAppService
import com.example.ringer_app.service.RingerAppService.Companion.LOCATION_TRIGGER
import com.example.ringer_app.service.ServiceHelper
import com.example.ringer_app.utils.LocationBroadcastReceiver
import com.example.ringer_app.repository.viewmodel.AddressViewModel
import com.example.ringer_app.repository.viewmodel.LocationViewModel
import com.example.ringer_app.repository.viewmodel.ProfileViewModel
import com.example.ringer_app.utils.ConnectionLiveData

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mPermissionResultLauncher: ActivityResultLauncher<String>
    private lateinit var mServiceHelper: ServiceHelper
    private lateinit var mReceiver: LocationBroadcastReceiver
    private var doubleBackToExitPressedOnce = false

    private val mLocationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        mPermissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                Log.d(TAG, "onCreate: $it")
            }
        mLocationViewModel.requestLocation(this)

        requestLocationPermissions()
        requestDoNotDisturbPermissions()

        mReceiver = LocationBroadcastReceiver(mLocationViewModel)

        IntentFilter(LOCATION_TRIGGER).also {
            registerReceiver(mReceiver, it)
        }

        setContentView(mBinding.root)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(mReceiver)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)

    }

    private fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            mPermissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

            Log.d(TAG, "requestLocationPermissions: false")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mPermissionResultLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }

        } else {
            mServiceHelper = ServiceHelper(this)

            val isServiceRunning = isServiceAlreadyRunning(RingerAppService.SHORT_CLASSNAME)
            Log.d(TAG, "requestLocationPermissions: $isServiceRunning")
            if (!isServiceRunning) mServiceHelper.startLocationService()
        }
    }

    private fun requestDoNotDisturbPermissions() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (!nm.isNotificationPolicyAccessGranted) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }
    }

    private fun isServiceAlreadyRunning(name: String): Boolean {
        val manager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (name == service.service.shortClassName) {
                Log.d(TAG, "requestLocationPermissions: ${service.service.shortClassName}")
                return true
            }
        }
        return false
    }

    companion object {
        val TAG = MainActivity::class.java.toString()
    }
}