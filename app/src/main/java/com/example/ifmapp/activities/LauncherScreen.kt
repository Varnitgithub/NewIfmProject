package com.example.ifmapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.ifmapp.R
import com.example.ifmapp.checked
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.GlobalLocation
import com.example.ifmapp.utils.UtilModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LauncherScreen : AppCompatActivity() {
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var LOCATION_PERMISSION_REQUEST_CODE = 111
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher_screen)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setIntervalMillis(500)
                .build()
        createLocationRequest()
        Log.d("TAAAAAAAAAAAAA", "getLastLocation: this is global loc. ${GlobalLocation.location}")


        if (checkPermission()) {
            CoroutineScope(Dispatchers.Default).launch {
                getLastLocation()
            }
            val delayMillis = 2000L
            Handler().postDelayed({
                finish()
                checkDatabaseUsers()
            }, delayMillis)
        } else {
            requestPermission()
        }


    }

    private fun checkDatabaseUsers() {
        val allUsers = SaveUsersInSharedPreference.getList(this)

        if (allUsers.isNotEmpty()) {
            startActivity(Intent(this@LauncherScreen, DashBoardScreen::class.java))
        } else {
            startActivity(Intent(this@LauncherScreen, RegistrationScreen::class.java))
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
CoroutineScope(Dispatchers.Default).launch {
    getLastLocation()
}
                val delayMillis = 3000L
                Handler().postDelayed({
                    finish()
                    checkDatabaseUsers()
                }, delayMillis)


            } else {
                CustomToast.showToast(this@LauncherScreen, "please allow for location")
            }
            //Permission Granted


        }
    }

    @SuppressLint("MissingPermission")
    fun createLocationRequest() {
        try {
            fusedLocationProviderClient?.requestLocationUpdates(
                LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 1000 // Update interval in milliseconds
                    fastestInterval = 500 // Fastest update interval in milliseconds
                },
                locationCallback!!,
                null
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeLocationUpdates() {
        locationCallback.let {
            if (it != null) {
                fusedLocationProviderClient?.removeLocationUpdates(it)
            }
        }

    }

    @SuppressLint("MissingPermission")
    suspend fun getLastLocation() {
        fusedLocationProviderClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                // Got last known location
                location?.let {
                    GlobalLocation.location = UtilModel(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        location.altitude.toString()
                    )

                    Log.d("TAAAAAAAAAAAAA", "getLastLocation: this is global loc. ${GlobalLocation.location}")
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
    }
}