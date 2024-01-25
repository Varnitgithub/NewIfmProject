package com.example.ifmapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.CheckInScreen
import com.example.ifmapp.activities.GnereratePinCodeScreen
import com.example.ifmapp.activities.MobileRegisterScreen
import com.example.ifmapp.adapters.AddAccountAdapter
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databasedb.EmployeeDB
import com.example.ifmapp.databasedb.EmployeePin
import com.example.ifmapp.databasedb.EmployeePinDao
import com.example.ifmapp.databinding.FragmentHomeBinding
import com.example.ifmapp.databinding.MainActivityBinding
import com.example.ifmapp.modelclasses.AddAccountModel
import com.example.ifmapp.modelclasses.ShiftTimingDetails
import com.example.ifmapp.modelclasses.geomappedsite_model.GeoMappedResponse
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponseItem
import com.example.ifmapp.modelclasses.shift_selection_model.ShiftSelectionResponse
import com.example.ifmapp.shared_preference.MyPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment(private var context: Context, private var otp: String) : Fragment(),
    AddAccountAdapter.OnClickedInterface {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var retrofitInstance: ApiInterface
    private lateinit var employeeDB: EmployeeDB
    private lateinit var otpLiveData: MutableLiveData<String>
    private lateinit var addAccountAdapter: AddAccountAdapter
    private var mobileNumber: String? = null
    private var empNumber: String? = null
    private lateinit var myPreferences: MyPreferences
    private lateinit var employeePinDao: EmployeePinDao
    private lateinit var retrofitInstace: ApiInterface
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

    private lateinit var sharedPref2: SharedPreferences
    private val sharedPrefFile2 = "com.example.myapp.PREFERENCE_FILE_KEY2"
    private lateinit var sharedPref3: SharedPreferences
    private val sharedPrefFile3= "com.example.myapp.PREFERENCE_FILE_KEY3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref2 = requireActivity().getSharedPreferences(sharedPrefFile2, Context.MODE_PRIVATE)
        sharedPref3 = requireActivity().getSharedPreferences(sharedPrefFile3, Context.MODE_PRIVATE)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        Log.d("TAGGGGGGGGGG", "onCreateView: $otp is the otppppppppp")
        employeePinDao = EmployeeDB.getInstance(requireContext()).employeePinDao()
        retrofitInstance = RetrofitInstance.apiInstance
        employeeDB = EmployeeDB.getInstance(context)
        addAccountAdapter = AddAccountAdapter(requireContext(), this)
        otpLiveData = MutableLiveData()
        myPreferences = MyPreferences(requireContext())
        hashMap = HashMap()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        createLocationRequest()
        binding.checkoutBtn.isEnabled = false
        employeePinDao = EmployeeDB.getInstance(requireContext()).employeePinDao()
        retrofitInstace = RetrofitInstance.apiInstance

        siteList = ArrayList()
        shiftList = ArrayList()
        shiftTimingList = ArrayList()

        getLastLocation()

        binding.checkInBtn.setOnClickListener {
            binding.checkoutBtn.setTextColor(resources.getColor(R.color.check_btn))
            binding.checkoutBtn.setBackgroundResource(R.drawable.button_backwhite)
            binding.checkInBtn.setTextColor(resources.getColor(R.color.white))
            binding.checkInBtn.setBackgroundResource(R.drawable.button_back)

            val intent = Intent(requireContext(), CheckInScreen::class.java)
            intent.putExtra("mPIN", otp)
            intent.putExtra("siteSelect", siteSelect)
            intent.putExtra("shiftSelect", shiftSelect)
            intent.putExtra("empNumber", empNumber)
            val editor = sharedPref2.edit()
            editor.putString("mPIN", otp)
            editor.putString("siteSelect", siteSelect)
            editor.putString("shiftSelect", shiftSelect)
            editor.putString("empNumber", empNumber)

            editor.apply()
            startActivity(intent)
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


    private fun getEmployee(otp: String) {

        employeePinDao.getcurrentEmployeeDetails(otp)
            .observe(requireActivity()) {
                if (it != null) {
                    Log.d("TAGGGGGG", "onTextChanged:it is not nulllll")

                    binding.userName.text = it.EmpName
                    binding.designation.text = it.Designation
                    empNumber = it.EmpNumber

                    if (mLatitude != null && mLongitude != null) {
                        shiftSelections(it.LocationAutoID)

                        Log.d("TAGG", "getEmployee: aaaaaaaaaaaaaa")
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
                        Log.d("TAGGGGGGG", "onResponse: responseeeee is success")
                        var size = response.body()?.size?.minus(1)
                        for (i in 0..size!!) {
                            shiftList.add(response.body()!!.get(i).ShiftCode)
                            shiftTimingList.add(response.body()!!.get(i).ShiftDetails)
                            val key = response.body()!!.get(i).ShiftCode
                            val value = response.body()!!.get(i).ShiftDetails
                            hashMap.put(key, value)

                            retrofitInstace.getGeoMappedSites(
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

                    // shift code = selected item
                    // shift timing = shiftTiming

                    CoroutineScope(Dispatchers.IO).launch {
                        employeePinDao.insertShiftTiming(
                            ShiftTimingDetails(
                                id = 1,
                                shiftCode = selectedItem,
                                shiftTiming = shiftTiming
                            )
                        )
                    }

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
        Log.d("TAGGGG", "getLastLocation: i got location")
        fusedLocationProviderClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                // Got last known location
                location?.let {
                    mLatitude = location.latitude.toString()
                    mLongitude = location.longitude.toString()
                    Log.d("TAGGGGGGG", "getLastLocation: $mLatitude $mLongitude asasas")

                    val editor = sharedPref3.edit()
                    editor.putString("mLatitude", mLatitude)
                    editor.putString("mLongitude", mLongitude)
                    editor.apply()
                    if (otp.isNotEmpty()) {

                        getEmployee(otp)

                    }
                }
            }

    }
    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
    }

    @SuppressLint("MissingPermission")


    override fun onclick(employeeModel: LoginByPINResponseItem, position: Int) {
        TODO("Not yet implemented")
    }


}