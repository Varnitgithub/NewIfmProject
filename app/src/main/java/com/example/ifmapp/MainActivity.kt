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
import androidx.lifecycle.lifecycleScope
import com.example.ifmapp.activities.CheckInScreen
import com.example.ifmapp.activities.ScannerScreen
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databasedb.EmployeeDB
import com.example.ifmapp.databasedb.EmployeePinDao
import com.example.ifmapp.databinding.MainActivityBinding
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
    private lateinit var employeePinDao: EmployeePinDao
    private var otp: String? = null
    private lateinit var retrofitInstace: ApiInterface
    private lateinit var shiftList: ArrayList<String>
    private lateinit var siteList: ArrayList<String>
    private var shiftSelect: String? = null
    private var siteSelect: String? = null
    private var empNumber: String? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var mLatitude: String? = null
    private var mLongitude: String? = null
    private var mAltitude: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setIntervalMillis(500)
                .build()
        getLastLocation()


//        locationCallback = object : LocationCallback() {
//            override fun onLocationAvailability(p0: LocationAvailability) {
//                super.onLocationAvailability(p0)
//            }
//
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//
//                // Use a coroutine to perform location-related tasks asynchronously
//
//                val location = locationResult.lastLocation
//                mLatitude = location?.latitude.toString()
//                mLongitude = location?.longitude.toString()
//                mAltitude = location?.altitude.toString()
//
//
//                // Use suspend functions or other asynchronous mechanisms if needed
//
//
//            }
//
//
//        }


        binding.checkoutBtn.isEnabled = false
        employeePinDao = EmployeeDB.getInstance(this).employeePinDao()
        retrofitInstace = RetrofitInstance.apiInstance

        siteList = ArrayList()
        shiftList = ArrayList()
        otp = intent.getStringExtra("mPIN")
        empNumber = intent.getStringExtra("empNumber")



        binding.checkInBtn.setOnClickListener {
            binding.checkoutBtn.setTextColor(resources.getColor(R.color.check_btn))
            binding.checkoutBtn.setBackgroundResource(R.drawable.button_backwhite)
            binding.checkInBtn.setTextColor(resources.getColor(R.color.white))
            binding.checkInBtn.setBackgroundResource(R.drawable.button_back)

            val intent = Intent(this, CheckInScreen::class.java)
            intent.putExtra("mPIN", otp)
            intent.putExtra("siteSelect", siteSelect)
            intent.putExtra("shiftSelect", shiftSelect)
            intent.putExtra("empNumber", empNumber)
            startActivity(intent)
        }
        // createLocationRequest()

        binding.checkoutBtn.setOnClickListener {
            binding.checkoutBtn.isEnabled = false
            binding.checkInBtn.setTextColor(resources.getColor(R.color.check_btn))
            binding.checkInBtn.setBackgroundResource(R.drawable.button_backwhite)
            binding.checkoutBtn.setTextColor(resources.getColor(R.color.white))
            binding.checkoutBtn.setBackgroundResource(R.drawable.button_back)

            startActivity(Intent(this, CheckInScreen::class.java))

        }


    }

    private fun getEmployee(otp: String) {

        employeePinDao.getcurrentEmployeeDetails(otp)
            .observe(this) {
                if (it != null) {
                    Log.d("TAGGGGGG", "onTextChanged:it is not null")

                    binding.userName.text = it.EmpName
                    binding.designation.text = it.Designation

                    if (mLatitude != null && mLongitude != null) {
                        shiftSelections(it.LocationAutoID)

                    }


                } else {
                    Log.d("TAGG", "getEmployee: ")
                }
            }
    }


    private fun shiftSelections(locationAutoid: String) {
        Log.d("TAGGGGGGGGGGGGG", "shiftSelections: $locationAutoid is location id")
        retrofitInstace.shiftSelectionApi("sams", locationAutoid)
            .enqueue(object : Callback<ShiftSelectionResponse?> {
                override fun onResponse(
                    call: Call<ShiftSelectionResponse?>,
                    response: Response<ShiftSelectionResponse?>
                ) {
                    if (response.isSuccessful) {
                        var size = response.body()?.size?.minus(1)
                        for (i in 0..size!!) {
                            shiftList.add(response.body()!!.get(i).ShiftCode)

                            retrofitInstace.getGeoMappedSites(
                                "sams", locationAutoid, "28.4062994",
                                "77.0685759"
                            ).enqueue(object : Callback<GeoMappedResponse?> {
                                override fun onResponse(
                                    call: Call<GeoMappedResponse?>,
                                    response: Response<GeoMappedResponse?>
                                ) {
                                    if (response.isSuccessful) {

                                        var sizes = response.body()?.size?.minus(1)
                                        for (i in 0..sizes!!) {
                                            siteList.add(response.body()!!.get(i).Clientcodename)
                                        }
                                        setSpinner(siteList, shiftList)


                                    }
                                }

                                override fun onFailure(
                                    call: Call<GeoMappedResponse?>,
                                    t: Throwable
                                ) {

                                }
                            })


                        }

                        Log.d("TAGGGGGG", "onResponse: site = $siteList and shift = $shiftList")


                    } else {
                        Log.d("TAGGGGGG", "onResponse: not getting successful response")
                    }
                }

                override fun onFailure(call: Call<ShiftSelectionResponse?>, t: Throwable) {
                    Log.d("TAGGGGGG", "onFailure: response failed")

                }
            })
    }

    fun setSpinner(site: ArrayList<String>, shift: ArrayList<String>) {
        // Define the items for the dropdown


        val adapterSelectionSite = ArrayAdapter(
            this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            site
        )
        val adapterSelectionShift = ArrayAdapter(
            this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            shift
        )
        adapterSelectionSite.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapterSelectionShift.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectSite.adapter = adapterSelectionSite
        binding.spinnerSelectShift.adapter = adapterSelectionShift

        // Set item selected listener for the spinner
        binding.spinnerSelectSite.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Handle the selected item
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    siteSelect = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing here
                }
            }

        binding.spinnerSelectShift.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Handle the selected item
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    shiftSelect = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing here
                }
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

    private fun removeLocationUpdates() {
        locationCallback.let {
            if (it != null) {
                fusedLocationProviderClient?.removeLocationUpdates(it)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        fusedLocationProviderClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                // Got last known location
                location?.let {
                    mLatitude = location.latitude.toString()
                    mLongitude = location.longitude.toString()
                    if (otp.toString().isNotEmpty()) {

                        otp?.let { getEmployee(it) }

                    }
                }
            }

    }
}
