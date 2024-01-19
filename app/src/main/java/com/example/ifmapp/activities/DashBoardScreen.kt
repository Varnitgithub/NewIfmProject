package com.example.ifmapp.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityDashBoardScreenBinding
import com.example.ifmapp.fragments.DocsFragment
import com.example.ifmapp.fragments.HomeFragment
import com.example.ifmapp.fragments.MenuFragment
import com.example.ifmapp.fragments.MustersFragment
import com.example.ifmapp.services.LocationService

class DashBoardScreen : AppCompatActivity() {
    private lateinit var binding: ActivityDashBoardScreenBinding
    private lateinit var fragmentManager: FragmentManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private val BACKGROUND_LOCATION_CODE = 111


    private val mustersFragment by lazy { MustersFragment(this) }
    private val docsFragment by lazy { DocsFragment() }
    private val menuFragment by lazy { MenuFragment() }
    private val homeFragment = HomeFragment(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board_screen)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board_screen)

        val mobileNumber = intent.getStringExtra("usermobileNumber")
        val pin = intent.getStringExtra("PIN")

        Log.d("TAGGGGGGGGG", "onCreate: $pin and $mobileNumber")

        if (!isMockLocation()) {
            if (checkPermission()) {


                val bundle = Bundle().apply {
                    putString("MOBILE_NUMBER", mobileNumber)
                    putString("PIN", pin)
                }
                homeFragment.arguments = bundle
                addFragment(homeFragment)
               // startService(Intent(this@DashBoardScreen,LocationService::class.java))
            } else {
                requestLocation()
            }
        } else {
            Toast.makeText(this, "mock location is enabled please disable it", Toast.LENGTH_SHORT)
                .show()
            requestLocation()
        }



        binding.bottomNavigation.itemTextColor =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
        binding.bottomNavigation.itemIconTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    addFragment(homeFragment)
                    true
                }

                R.id.navigation_musters -> {
                    addFragment(mustersFragment)
                    true
                }

                R.id.navigation_mydocs -> {
                    addFragment(docsFragment)

                    true
                }

                R.id.navigation_menu -> {
                    addFragment(menuFragment)

                    true
                }

                else -> {
                    false
                }

            }
        }


    }

    private fun isMockLocation(): Boolean {
        try {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            return location?.isFromMockProvider ?: false
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        return false
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()

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

    private fun requestLocation() {

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
                if (!isMockLocation()) {
                    addFragment(homeFragment)
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
}



