package com.example.ifmapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat

class CheckInternetConnection {

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }

    fun GetLocation(ctx: Context): Location? {
        var final_location: Location? = null
        val lManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        val locationGPS = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        var locationNet: Location? = null
        val netEnabled = lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (netEnabled) {
            if (ActivityCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return null
            }
            locationNet = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            final_location = locationNet
            println("Location value in net enabled $final_location")
            Log.d("TAGGGGGGGGG", "GetLocation: $final_location is the loc1")

        }
        if (final_location == null) {
            final_location = locationGPS
        }
        println("Location value is $final_location")
        Log.d("TAGGGGGGGGG", "GetLocation: $final_location is the loc")
        return final_location
    }
}