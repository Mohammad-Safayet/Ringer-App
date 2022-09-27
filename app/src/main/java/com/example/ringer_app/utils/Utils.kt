package com.example.ringer_app.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.asLiveData
import com.example.ringer_app.R
import com.example.ringer_app.repository.model.Address
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.log

object Utils {

    val formatEqn1 = SimpleDateFormat("HH:m")
    val formatEqn2 = SimpleDateFormat("hh:mm aa")
    val formatEqn3 = SimpleDateFormat("HH:mm")
    private val formatEqn4: DateTimeFormatter = DateTimeFormatter.ofPattern(
        "hh:mm a",
        Locale.US
    )

    @JvmStatic
    fun getTimeDifference(startTime: String, endTime: String) {
        val startResult = LocalTime.parse(startTime, formatEqn4)
        val endResult = LocalTime.parse(endTime, formatEqn4)

        val result = startResult.isBefore(endResult)
        Log.d("openTimePicker", "getTimeDifference: $result")
    }

    @JvmStatic
    fun getAddress(context: Context, latitude: Double, longitude: Double): Address {
        val geoCoder = Geocoder(context, Locale.getDefault())
        Log.d("Debug", "getAddress: $longitude $latitude")

        val fromLocation = geoCoder.getFromLocation(latitude, longitude, 3)

        val address = Address(
            longitude,
            latitude,
            fromLocation[0].countryName,
            fromLocation[0].locality,
            fromLocation[0].getAddressLine(0)
        )
        Log.d("Address", "getAddress: $address")


        return address
    }

    @JvmStatic
    fun generateNotification(context: Context): Notification {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID, "Ringer App", NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(notificationChannel)

        val notificationCompatBuilder =
            NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)

        return notificationCompatBuilder
            .setContentTitle("Ringer App")
            .setContentText("Location tracking is active")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }
}