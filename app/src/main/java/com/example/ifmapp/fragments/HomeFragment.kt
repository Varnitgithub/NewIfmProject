package com.example.ifmapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.CheckInScreen
import com.example.ifmapp.activities.RegistrationScreen
import com.example.ifmapp.adapters.AddAccountAdapter
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.FragmentHomeBinding
import com.example.ifmapp.modelclasses.geomappedsite_model.GeoMappedResponse
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.shift_selection_model.ShiftSelectionResponse
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.shared_preference.shared_preference_models.CurrentUserShiftsDetails
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment(
    private var context: Context,
    private var otp: String,
    private var mobileNumber: String,
    private var empNumber: String
) : Fragment(),
    AddAccountAdapter.OnClickedInterface {
    private lateinit var binding: FragmentHomeBinding
    private var retrofitInstance: ApiInterface = RetrofitInstance.apiInstance
    private lateinit var addAccountAdapter: AddAccountAdapter

    private lateinit var shiftList: ArrayList<String>
    private lateinit var shiftTimingList: ArrayList<String>
    private lateinit var siteList: ArrayList<String>
    private var shiftSelect: String? = null
    private var shiftSelectTiming: String? = null
    private var siteSelect: String? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var mLatitude: String? = null
    private var mLongitude: String? = null
    private var mAltitude: String? = null
    private lateinit var hashMap: HashMap<String, String>
    private var currentUser: UserListModel? = null

    private var empDesignation: String? = null

    private var empName: String? = null

    private var locationAutoId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        addAccountAdapter = AddAccountAdapter(requireContext(), this)
        hashMap = HashMap()


        SaveUsersInSharedPreference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        Log.d("TAGGGG", "onCreateView:////////////////////////// $otp")
        currentUser = SaveUsersInSharedPreference.getUserByPin(requireContext(), otp)
        mobileNumber = currentUser?.mobileNumber.toString()
        val allusers =  SaveUsersInSharedPreference.getList(requireContext())
        Log.d("TAGGGGGGGGGGG", "onCreateView:/.........all usersz ${allusers.size}")

        for (i in 0 until allusers.size){
            Log.d("TAGGGGGGGGGGG", "onCreateView:/.........all usersz ${allusers.get(i)}")

        }
        getCurrentEmployee(otp, mobileNumber)

        binding.userName.text = currentUser?.userName

        createLocationRequest()
        binding.checkoutBtn.isEnabled = false
        retrofitInstance = RetrofitInstance.apiInstance


        binding.btnLogout.setOnClickListener {
            SaveUsersInSharedPreference.removeUserByPin(requireContext(),otp)

        startActivity(Intent(requireContext(),RegistrationScreen::class.java))
        }
        siteList = ArrayList()
        shiftList = ArrayList()
        shiftTimingList = ArrayList()

        getLastLocation()

        binding.checkInBtn.setOnClickListener {
            if (shiftSelect!=null&&siteSelect!=null){
                binding.checkoutBtn.setTextColor(resources.getColor(R.color.check_btn))
                binding.checkoutBtn.setBackgroundResource(R.drawable.button_backwhite)
                binding.checkInBtn.setTextColor(resources.getColor(R.color.white))
                binding.checkInBtn.setBackgroundResource(R.drawable.button_back)

                val currentUserShiftList = ArrayList<CurrentUserShiftsDetails>()

                val currentUserShift = CurrentUserShiftsDetails(
                    shiftSelect.toString(),
                    siteSelect.toString(),
                    otp,
                    empNumber,empDesignation.toString(),empName.toString(),locationAutoId.toString()
                    ,shiftTimingList)
                currentUserShiftList.add(currentUserShift)
                SaveUsersInSharedPreference.saveCurrentUserShifts(
                    requireContext(),
                    currentUserShiftList
                )

                Log.d(
                    "TAGGGGGG",
                    "onCreateView: 1111111 saved successfullly"
                )
                startActivity(Intent(requireContext(), CheckInScreen::class.java))
            }else{
                Toast.makeText(requireContext(), "Please select Shift and Site", Toast.LENGTH_SHORT).show()
            }

        }


        binding.checkoutBtn.setOnClickListener {
            binding.checkoutBtn.isEnabled = false
            binding.checkInBtn.setTextColor(resources.getColor(R.color.check_btn))
            binding.checkInBtn.setBackgroundResource(R.drawable.button_backwhite)
            binding.checkoutBtn.setTextColor(resources.getColor(R.color.white))
            binding.checkoutBtn.setBackgroundResource(R.drawable.button_back)

            startActivity(Intent(requireContext(), CheckInScreen::class.java))

        }
        return binding.root
    }


    private fun shiftSelections(locationAutoid: String) {
        Log.d("TAGGGGGGGGGGGGG", "shiftSelections: $locationAutoid is location id")
        retrofitInstance.shiftSelectionApi("sams", locationAutoid)
            .enqueue(object : Callback<ShiftSelectionResponse?> {
                override fun onResponse(
                    call: Call<ShiftSelectionResponse?>,
                    response: Response<ShiftSelectionResponse?>
                ) {
                    if (response.isSuccessful) {
                        Log.d("TAGGGGGGG", "onResponse: responseeeee is success")
                        val size = response.body()?.size?.minus(1)
                        for (i in 0..size!!) {
                            shiftList.add(response.body()!!.get(i).ShiftCode)
                            shiftTimingList.add(response.body()!!.get(i).ShiftDetails)

                        }
                        Log.d(
                            "TAGGGGGGG",
                            "onResponse: this is shift timing list ...............${
                                shiftList
                            }"
                        )
                        Log.d(
                            "TAGGGGGGG",
                            "onResponse: this is shift  list ...............${
                                shiftTimingList
                            }"
                        )
                        retrofitInstance.getGeoMappedSites(
                            "sams", locationAutoid, "28.4062994",
                            "77.0685759"
                        ).enqueue(object : Callback<GeoMappedResponse?> {
                            override fun onResponse(
                                call: Call<GeoMappedResponse?>,
                                response: Response<GeoMappedResponse?>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d(
                                        "TAGGGGGGG",
                                        "onResponse:qqqqqqqqqq responseeeee is success"
                                    )

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


                }



                override fun onFailure(call: Call<ShiftSelectionResponse?>, t: Throwable) {

                }
            })
    }

    fun setSpinner(site: ArrayList<String>, shift: ArrayList<String>) {
        // Define the items for the dropdown


        val adapterSelectionSite = ArrayAdapter(
            requireActivity(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            site
        )
        val adapterSelectionShift = ArrayAdapter(
            requireContext(),
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
                    parent?.let {
                        if (it.count > 0) {
                            siteSelect = it.getItemAtPosition(0).toString()
                        }
                    }
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

                    val shiftTiming = hashMap[selectedItem]


                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parent?.let {
                        if (it.count > 0) {
                            shiftSelect = it.getItemAtPosition(0).toString()
                        }
                    }
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

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                // Got last known location
                location?.let {
                    mLatitude = location.latitude.toString()
                    mLongitude = location.longitude.toString()
                    mAltitude = location.altitude.toString()

                }
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
    }

    @SuppressLint("MissingPermission")


    override fun onclick(employeeModel: UserListModel, position: Int) {

    }

    private fun getCurrentEmployee(otp: String, mobileNumber: String) {
        retrofitInstance.loginByPIN("sams", mobileNumber, otp)
            .enqueue(object : Callback<LoginByPINResponse?> {
                override fun onResponse(
                    call: Call<LoginByPINResponse?>,
                    response: Response<LoginByPINResponse?>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.get(0)?.MessageID.toString().toInt() == 1) {

                            locationAutoId = response.body()?.get(0)?.LocationAutoID
                            binding.userName.text = response.body()?.get(0)?.EmpName
                            binding.designation.text = response.body()?.get(0)?.Designation

                            empDesignation = response.body()?.get(0)?.Designation
                            empName = response.body()?.get(0)?.EmpName
                            shiftSelections(locationAutoId.toString())
                        }
                    } else {
                        Log.d("TAGGGGGG", "onResponse: response id not successful")
                    }
                }

                override fun onFailure(call: Call<LoginByPINResponse?>, t: Throwable) {

                }
            })
    }


}