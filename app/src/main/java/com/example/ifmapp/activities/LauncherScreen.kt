package com.example.ifmapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ifmapp.R
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.CheckInternetConnection
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


//AAAAFXAqqtg:APA91bEtVKr-vzafAfGINsli_ysT-_0zKwHVGhhH_1Wdtp4QqP2MAhhoSM39129kVN7HWxJcVi1RyAbbGlJsp-8z41zUeDb6sXVpg6IcpfRUVS5TOAEqFUPFLIcuQZRvbli3JrXI2o0Q
//this is my firebase server key for notification
class LauncherScreen : AppCompatActivity() {
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var LOCATION_PERMISSION_REQUEST_CODE = 111
    private val CAMERA_REQUEST_CODE = 11
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher_screen)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setIntervalMillis(500)
                .build()
        createLocationRequest()
        Log.d("TAAAAAAAAAAAAA", "getLastLocation: this is global loc. ${GlobalLocation.location}")

        /*val checkoutReceiver = CheckoutReceiver()
        val checkoutTime = System.currentTimeMillis() + 1 * 60 * 1000 // 1 minutes from now
        checkoutReceiver.setCheckoutTime(this, checkoutTime)
        checkoutReceiver.checkCheckoutTime(this@LauncherScreen)*/



        if (checkPermission()) {
            val welcomeText: TextView = findViewById(R.id.welcomeText)
            welcomeText.text = "Welcome to GroupL" // Set your dynamic text here
            welcomeText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in))
            welcomeText.visibility = TextView.VISIBLE
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
                val welcomeText: TextView = findViewById(R.id.welcomeText)
                welcomeText.text = "Welcome to GroupL" // Set your dynamic text here
                welcomeText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in))
                welcomeText.visibility = TextView.VISIBLE
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

    /*protected fun createLocationRequestz() {
        mLocationRequest = LocationRequest()
        mLocationRequest.setInterval(com.example.lenovo.sam.GeoAdressList.GeoAdress.INTERVAL)
        mLocationRequest.setFastestInterval(com.example.lenovo.sam.GeoAdressList.GeoAdress.FASTEST_INTERVAL)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }*/

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
                    CheckInternetConnection().GetLocation(this@LauncherScreen)?.latitude.toString()
                    CheckInternetConnection().GetLocation(this@LauncherScreen)?.longitude.toString()
                    CheckInternetConnection().GetLocation(this@LauncherScreen)?.altitude.toString()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

}