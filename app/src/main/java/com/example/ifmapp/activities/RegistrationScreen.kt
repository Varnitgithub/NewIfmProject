package com.example.ifmapp.activities

import LocationSpoofChecker
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ifmapp.R
import com.example.ifmapp.toast.CustomToast

class RegistrationScreen : AppCompatActivity() {
    private var LOCATION_PERMISSION_REQUEST_CODE = 111
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_registration_screen)

        val newRegistration: Button = findViewById(R.id.new_Registration)

        val alreadyRegistered: Button = findViewById(R.id.already_Registered)

            if (checkPermission())  {

if (LocationSpoofChecker.isLocationSpoofed(this@RegistrationScreen)){
    Toast.makeText(this@RegistrationScreen, "You are using location spoofed app, Please disable this", Toast.LENGTH_SHORT).show()
}
            } else {
                requestPermission()
            }
            newRegistration.setOnClickListener {
//                if (LocationSpoofChecker.isLocationSpoofed(this@RegistrationScreen)){
//                    Toast.makeText(this@RegistrationScreen, "You are using location spoofed app, Please disable this", Toast.LENGTH_SHORT).show()
//                }else{
                    startActivity(Intent(this, SignUpWaysScreen::class.java))

                }

            alreadyRegistered.setOnClickListener {
//                if (LocationSpoofChecker.isLocationSpoofed(this@RegistrationScreen)){
//                    Toast.makeText(this@RegistrationScreen, "You are using location spoofed app, Please disable this", Toast.LENGTH_SHORT).show()
//                }
//                else{
                startActivity(Intent(this, LoginCheckedScreen::class.java))

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
                if (LocationSpoofChecker.isLocationSpoofed(this@RegistrationScreen)){
                CustomToast.showToast(this@RegistrationScreen,"You are using location spoofed app, Please disable this")
                }
            } else {
               CustomToast.showToast(this@RegistrationScreen,"please allow for location")
            }
            //Permission Granted


        }
    }

}