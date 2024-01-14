package com.example.ifmapp.activities

import LocationServiceClass
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
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

class DashBoardScreen : AppCompatActivity() {
    private lateinit var binding: ActivityDashBoardScreenBinding
    private lateinit var fragmentManager: FragmentManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private val BACKGROUND_LOCATION_CODE = 111


    private val homeFragment by lazy { HomeFragment() }
    private val mustersFragment by lazy { MustersFragment() }
    private val docsFragment by lazy { DocsFragment() }
    private val menuFragment by lazy { MenuFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board_screen)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board_screen)

        val homeFragment = HomeFragment()
        val mustersFragment = MustersFragment()
        val docsFragment = DocsFragment()
        val menuFragment = MenuFragment()
        if (checkPermission()) {

            addFragment(homeFragment)
        } else {
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
                //Permission Granted
                addFragment(homeFragment)

            }
        }
    }
}



