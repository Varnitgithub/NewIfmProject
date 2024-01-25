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

class RegistrationScreen : AppCompatActivity() {
    private lateinit var employeePinDao: EmployeePinDao
    private var LOCATION_PERMISSION_REQUEST_CODE = 111
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        employeePinDao = EmployeeDB.getInstance(this).employeePinDao()
        setContentView(R.layout.activity_registration_screen)
        if (!checked.isChecked) {
            checked.isChecked = true
            val newRegistration: Button = findViewById(R.id.new_Registration)
            val alreadyRegistered: Button = findViewById(R.id.already_Registered)


            if (checkPermission()) {
                checkReceiverData()
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


    }

    private fun checkReceiverData() {
        employeePinDao.getAllEmployeeDetails().observe(this@RegistrationScreen) {
            if (it.isNotEmpty()) {
                startActivity(Intent(this@RegistrationScreen, DashBoardScreen::class.java))
            }
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
                checkReceiverData()
            } else {
                Toast.makeText(
                    this,
                    "mock location is enabled please disable it",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //Permission Granted


        }
    }

}