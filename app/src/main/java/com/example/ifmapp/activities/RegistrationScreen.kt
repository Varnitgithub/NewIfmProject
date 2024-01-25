package com.example.ifmapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.checked
import com.example.ifmapp.databasedb.EmployeeDB
import com.example.ifmapp.databasedb.EmployeePinDao
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference

class RegistrationScreen : AppCompatActivity() {
    private var LOCATION_PERMISSION_REQUEST_CODE = 111
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checked.isChecked) {
       checkDatabaseUsers()
        }
        setContentView(R.layout.activity_registration_screen)

        val newRegistration: Button = findViewById(R.id.new_Registration)
        val alreadyRegistered: Button = findViewById(R.id.already_Registered)
            if (checkPermission()) {

            } else {
                requestPermission()
            }
            newRegistration.setOnClickListener {
                    startActivity(Intent(this, SignUpWaysScreen::class.java))



            }
            alreadyRegistered.setOnClickListener {
                    startActivity(Intent(this, LoginCheckedScreen::class.java))


            }
        }

    private fun checkDatabaseUsers() {
      val allUsers = SaveUsersInSharedPreference.getList(this)
        if (allUsers.isNotEmpty() ){
            startActivity(Intent(this@RegistrationScreen,DashBoardScreen::class.java))
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

            } else {
                Toast.makeText(
                    this,
                    "please allow for location",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //Permission Granted


        }
    }

}