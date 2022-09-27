package com.example.ringer_app.repository.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ringer_app.repository.model.Address
import com.example.ringer_app.service.RingerAppService
import com.example.ringer_app.utils.Utils
import com.google.android.gms.location.*

class LocationViewModel(
    application: Application
): AndroidViewModel(application) {
    private val TAG = LocationViewModel::class.java.toString()

    private val _address: MutableLiveData<Address> = MutableLiveData<Address>()
    val address: LiveData<Address> = _address

    fun setAddress(address: Address) {
        _address.value = address
    }

    fun printLog(): Unit {
        Log.d(TAG, "printLog: longitude ${address.value?.longitude} latitude ${address.value?.latitude}")
    }

    fun requestLocation(context: Context) {
        Log.d(TAG, "requestLocation: started")
        val mFusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(context)

        mFusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                _address.value = Utils.getAddress(context, it.latitude, it.longitude)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        Log.d(TAG, "requestLocation: started")
    }
}