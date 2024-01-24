package com.example.ifmapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.app.Activity
import androidx.annotation.RequiresApi

class IMEIGetter(private val context: Context) {

    fun getIMEI(): String {
        return if (hasReadPhoneStatePermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getIMEIForAndroid10AndAbove()
            } else {
                getIMEIForBelowAndroid10()
            }
        } else {
            // Request the necessary permission at runtime
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                PERMISSION_REQUEST_READ_PHONE_STATE
            )
            "Permission not granted for READ_PHONE_STATE"
        }
    }

    private fun hasReadPhoneStatePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getIMEIForBelowAndroid10(): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Android 8.0 (Oreo) and above, use getImei()
            try {
                telephonyManager.imei ?: "IMEI not available"
            } catch (e: SecurityException) {
                "IMEI not available due to security exception"
            }
        } else {
            // For versions below Android 8.0, use the deprecated getDeviceId()
            telephonyManager.deviceId ?: "IMEI not available"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getIMEIForAndroid10AndAbove(): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return try {
            telephonyManager.imei ?: "IMEI not available"
        } catch (e: SecurityException) {
            "IMEI not available due to security exception"
        }
    }

    companion object {
        const val PERMISSION_REQUEST_READ_PHONE_STATE = 123
    }
}
