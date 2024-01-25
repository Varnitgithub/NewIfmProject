package com.example.ifmapp

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import com.example.ifmapp.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ifmapp.activities.CheckInScreen
import com.example.ifmapp.activities.ScannerScreen
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databasedb.EmployeeDB
import com.example.ifmapp.databasedb.EmployeePinDao
import com.example.ifmapp.databinding.MainActivityBinding
import com.example.ifmapp.fragments.DocsFragment
import com.example.ifmapp.fragments.HomeFragment
import com.example.ifmapp.fragments.MenuFragment
import com.example.ifmapp.fragments.MustersFragment
import com.example.ifmapp.modelclasses.ShiftTimingDetails
import com.example.ifmapp.modelclasses.geomappedsite_model.GeoMappedResponse
import com.example.ifmapp.modelclasses.shift_selection_model.ShiftSelectionResponse
import com.example.ifmapp.modelclasses.shift_selection_model.ShiftSelectionResponseItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    private lateinit var hashMap: HashMap<String, String>
    private lateinit var homeFragment: HomeFragment
    private lateinit var mustersFragment: MustersFragment
    private lateinit var docsfragment: DocsFragment
    private lateinit var menuFragment: MenuFragment

    private var otp: String? = null

    private var mobileNumber: String? = null

    private var empNumber: String? = null

    private var locationAutoId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        otp = intent.getStringExtra("mPIN")
        mobileNumber = intent.getStringExtra("mobileNumber")
        empNumber = intent.getStringExtra("empId")



        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)


        homeFragment = HomeFragment(this, otp.toString(), mobileNumber.toString(),empNumber.toString())
        val bundle = Bundle()
        bundle.putString("mPIN", otp)
        homeFragment.arguments = bundle
        addFragment(homeFragment)

        mustersFragment = MustersFragment(this)
        docsfragment = DocsFragment()
        menuFragment = MenuFragment()

        hashMap = HashMap()


        // Set listener for item clicks
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    addFragment(homeFragment)
                    true
                }

                R.id.navigation_musters -> {
                    addFragment(mustersFragment)

                    true
                }

                R.id.navigation_mydocs -> {
                    addFragment(docsfragment)

                    true
                }

                R.id.navigation_menu -> {
                    addFragment(menuFragment)

                    true
                }

                else -> false
            }
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()

    }

}
