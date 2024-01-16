// LocationService.kt

package com.example.ifmapp.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import java.io.IOException
import java.util.*

class LocationService : Service() {

    inner class LocalBinder : Binder() {
        fun getService(): LocationService {
            return this@LocationService
        }
    }

    // LiveData for delivering location updates
    private val _locationLiveData = MutableLiveData<LocationData>()
    val locationLiveData: LiveData<LocationData>
        get() = _locationLiveData

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    data class LocationData(val latitude: String, val longitude: String, val address: String)

    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setFastestInterval(0)

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        } catch (e: SecurityException) {
            // Handle exception
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let { location ->
                handleLocationChanged(location)
            }
        }
    }

    private fun handleLocationChanged(location: Location) {
        val latitude = location.latitude.toString()
        val longitude = location.longitude.toString()
        val address = getAddressFromLocation(location.latitude, location.longitude)
        Log.d("TAGGGGG", "handleLocationChanged: $latitude, $longitude")

        // Update LiveData
        _locationLiveData.postValue(LocationData(latitude, longitude, address))
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        try {
            val addresses: MutableList<Address>? =
                geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                return "${address.adminArea}, ${address.locality}, ${address.subAdminArea}, ${address.subLocality}"
            }
        } catch (e: IOException) {
            // Handle exception
        }
        return ""
    }
}
