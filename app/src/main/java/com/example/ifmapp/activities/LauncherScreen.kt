package com.example.ifmapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.ifmapp.R
import com.example.ifmapp.checked
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference

class LauncherScreen : AppCompatActivity() {
    private var LOCATION_PERMISSION_REQUEST_CODE=111
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher_screen)

        if (checkPermission()){
            val delayMillis = 3000L
            Handler().postDelayed({
                finish()
                checkDatabaseUsers()
            }, delayMillis)
        }else{
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

                val delayMillis = 3000L
                Handler().postDelayed({
                    finish()
                    checkDatabaseUsers()
                }, delayMillis)


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