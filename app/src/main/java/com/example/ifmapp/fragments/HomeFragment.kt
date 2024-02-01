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
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.CheckInScreen
import com.example.ifmapp.activities.DashBoardScreen
import com.example.ifmapp.activities.RegistrationScreen
import com.example.ifmapp.adapters.AddAccountAdapter
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.FragmentHomeBinding
import com.example.ifmapp.modelclasses.daily_attendance_model.DailyAttendanceModel
import com.example.ifmapp.modelclasses.geomappedsite_model.GeoMappedResponse
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponse
import com.example.ifmapp.modelclasses.shift_selection_model.ShiftSelectionResponse
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.shared_preference.shared_preference_models.CurrentUserShiftsDetails
import com.example.ifmapp.toast.CustomToast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment(
    private var context: Context,
    private var otp: String,
    private var mobileNumber: String,
    private var empNumber: String, private var userName: String, private var inOut: String
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
    private var clientCode: String? = null
    private var asmtId: String? = null

    private var empName: String? = null

    private var attendanceStatus: String? = null

    private var locationAutoId: String? = null

    private lateinit var shiftSelectionLiveData: MutableLiveData<String>
    private lateinit var siteSelectionLiveData: MutableLiveData<String>

    private var outStatus: String? = null
    private var intTime: String? = null
    private var outTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        addAccountAdapter = AddAccountAdapter(requireContext(), this)
        hashMap = HashMap()

        shiftSelectionLiveData = MutableLiveData()
        siteSelectionLiveData = MutableLiveData()

//        attendanceStatus = SaveUsersInSharedPreference.getAttendanceStatus(requireActivity())
//
//        if (attendanceStatus.toString().isNotEmpty()&&attendanceStatus.toString()=="IN"){
//            binding.checkInBtn.isClickable= false
//            binding.checkoutBtn.isEnabled = true
//            binding.checkoutBtn.isClickable = true
//        }

        Log.d("TAGGGGG", "onCreate: this is newwww $otp and $userName")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        Log.d("TAGGGG", "onCreateView:////////////////////////// $otp")
        currentUser = SaveUsersInSharedPreference.getUserByPin(requireContext(), otp, userName)
        locationAutoId = currentUser?.LocationAutoId.toString()
        empName = currentUser?.userName



        empNumber = currentUser?.empId.toString()
        empDesignation = currentUser?.designation
        val allusers = SaveUsersInSharedPreference.getList(requireContext())
        Log.d("TAGGGGGGGGGGG", "onCreateView:/.........all usersz ${allusers.size}")



        shiftSelections(locationAutoId.toString())

        for (i in 0 until allusers.size) {
            Log.d("TAGGGGGGGGGGG", "onCreateView:/.........all usersz ${allusers.get(i)}")

        }
        //getCurrentEmployee(otp, mobileNumber)
        binding.userName.text = "${currentUser?.userName} ($empNumber)"
        binding.designation.text = currentUser?.designation

        binding.shiftStartdateText.text = getFormattedDate()
        binding.shiftEnddateText.text = getFormattedDate()
        binding.join.text = getFormattedDate()
        binding.out.text = getFormattedDate()

        createLocationRequest()
        binding.checkoutBtn.isEnabled = false
        binding.checkInBtn.isEnabled = false
        retrofitInstance = RetrofitInstance.apiInstance

        siteSelectionLiveData.observe(requireActivity()) {
        }

        shiftSelectionLiveData.observe(requireActivity()) {
            setShiftTiming(it)
        }
        binding.btnLogout.setOnClickListener {

            startActivity(Intent(requireContext(), DashBoardScreen::class.java))
        }
        siteList = ArrayList()
        shiftList = ArrayList()
        shiftTimingList = ArrayList()

        getLastLocation()


        binding.checkInBtn.setOnClickListener {
            if (shiftSelect != null && siteSelect != null) {
                binding.checkoutBtn.setTextColor(resources.getColor(R.color.check_btn))
                binding.checkoutBtn.setBackgroundResource(R.drawable.button_backwhite)
                binding.checkInBtn.setTextColor(resources.getColor(R.color.white))
                binding.checkInBtn.setBackgroundResource(R.drawable.button_back)

                val currentUserShiftList = ArrayList<CurrentUserShiftsDetails>()

                val currentUserShift = CurrentUserShiftsDetails(
                    shiftSelect.toString(),
                    siteSelect.toString(),
                    otp,
                    empNumber,
                    empDesignation.toString(),
                    empName.toString(),
                    locationAutoId.toString(),
                    shiftTimingList
                )
                currentUserShiftList.add(currentUserShift)
                SaveUsersInSharedPreference.saveCurrentUserShifts(
                    requireContext(),
                    currentUserShiftList
                )

                val intent = Intent(requireContext(), CheckInScreen::class.java)
                intent.putExtra("INOUTStatus", "IN")
                intent.putExtra("mPIN", otp)
                intent.putExtra("empName", userName)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Please select Shift and Site", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        binding.checkoutBtn.setOnClickListener {
            binding.checkoutBtn.isEnabled = false
            binding.checkInBtn.setTextColor(resources.getColor(R.color.check_btn))
            binding.checkInBtn.setBackgroundResource(R.drawable.button_backwhite)
            binding.checkoutBtn.setTextColor(resources.getColor(R.color.white))
            binding.checkoutBtn.setBackgroundResource(R.drawable.button_back)

            var intent = Intent(requireContext(), CheckInScreen::class.java)
            intent.putExtra("INOUTStatus", "OUT")
            intent.putExtra("mPIN", otp)
            intent.putExtra("empName", userName)
            startActivity(intent)
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
                    siteSelectionLiveData.postValue(siteSelect.toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parent?.let {
                        if (it.count > 0) {

                            siteSelect = it.getItemAtPosition(0).toString()
                            siteSelectionLiveData.postValue(siteSelect.toString())
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
                    shiftSelectionLiveData.postValue(shiftSelect.toString())
                    val shiftTiming = hashMap[selectedItem]

                    geoMappedUser()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parent?.let {
                        if (it.count > 0) {
                            shiftSelect = it.getItemAtPosition(0).toString()
                            shiftSelectionLiveData.postValue(shiftSelect.toString())
                            geoMappedUser()
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
                    geoMappedUser()
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

    private fun getFormattedDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEE d MMM", Locale.getDefault())
        return dateFormat.format(currentDate)
    }


    fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    private fun setShiftTiming(shift: String) {
        Log.d("TAGGGGGGGG", "setShiftTiming: $shiftTimingList")
        for (timing in shiftTimingList) {
            Log.d("TAGGGGGGGG", "setShiftTiming: one ${timing.substring(0, 2)}")
            Log.d("TAGGGGGGGG", "setShiftTiming: one ${shift} is my shift")
            if (shift.trim() == timing.substring(0, 2).trim()) {

                Log.d("TAGGGGGGGG", "setShiftTiming: one ${shift} is common shift")

                binding.shiftStartTime.text = timing.substring(4, 9)
                binding.shiftEndTime.text = timing.substring(10, 15)
            }
        }
    }

    private fun setShiftJoiningTime(inTime: String, outTime: String) {
        if (inTime.isEmpty()) {
            Log.d("TAGGLLLL", "setShiftJoiningTime:...........................calling 1133")

            binding.checkInBtn.isEnabled = true
            binding.checkoutBtn.isEnabled = false
            binding.joiningTime.text = "- - - -"
            binding.joiningAm.text = ""
            binding.outTime.text = "- - - -"
            binding.outPm.text = ""

            binding.spinnerSelectShift.isClickable = true
            binding.spinnerSelectSite.isClickable = true
        } else if (inTime.isNotEmpty()&&outTime.isEmpty()) {
            binding.spinnerSelectSite.isEnabled = false
            binding.spinnerSelectShift.isEnabled = false
            Log.d("TAGGLLLL", "setShiftJoiningTime:...........................calling 1144")
            binding.checkInBtn.setTextColor(resources.getColor(R.color.check_btn))
            binding.checkInBtn.setBackgroundResource(R.drawable.button_backwhite)
            binding.checkoutBtn.setTextColor(resources.getColor(R.color.white))
            binding.checkoutBtn.setBackgroundResource(R.drawable.button_back)
            binding.checkInBtn.isEnabled = false
            binding.checkoutBtn.isEnabled = true
            binding.joiningTime.text = inTime
            setAmPm(inTime,binding.joiningAm)

            binding.outTime.text = "----"
            binding.outPm.text = ""

            binding.spinnerSelectShift.isClickable = false
            binding.spinnerSelectSite.isClickable = false
        }else{
            Log.d("TAGGLLLL", "setShiftJoiningTime:...............................calling 1155")
            binding.checkInBtn.isEnabled = false
            binding.checkoutBtn.isEnabled = false
            binding.checkInBtn.setTextColor(resources.getColor(R.color.check_btn))
            binding.checkInBtn.setBackgroundResource(R.drawable.button_backwhite)
            binding.checkoutBtn.setTextColor(resources.getColor(R.color.check_btn))
            binding.checkoutBtn.setBackgroundResource(R.drawable.button_backwhite)
            binding.spinnerSelectSite.isEnabled = false
            binding.spinnerSelectShift.isEnabled = false

            binding.joiningTime.text = inTime
            setAmPm(inTime,binding.joiningAm)

            binding.outTime.text = outTime
            setAmPm(outTime,binding.outPm)

            binding.spinnerSelectShift.isClickable = false
            binding.spinnerSelectSite.isClickable = false
        }

    }
    private fun setAmPm(time:String,joinOut:TextView){
        if (time.isNotEmpty()){
            if ( time.substring(0,2).toInt()<12){
                joinOut.text = "AM"
            }else{
                joinOut.text = "PM"
            }
        }
    }

    private fun geoMappedUser() {

        Log.d("TAGGLLLL", "loginUser: .................$locationAutoId  $mAltitude  $mLongitude")
        retrofitInstance.getGeoMappedSites(
            "sams",
            locationAutoId.toString(),
            mLatitude.toString(),
            mLongitude.toString()
        ).enqueue(object : Callback<GeoMappedResponse?> {
            override fun onResponse(
                call: Call<GeoMappedResponse?>,
                response: Response<GeoMappedResponse?>
            ) {
                if (response.body()?.get(0)?.MessageID?.toInt() == 1) {
                    clientCode = response.body()?.get(0)!!.ClientCode
                    asmtId = response.body()?.get(0)!!.AsmtID
                    Log.d("TAGGLL", "onResponse: ${clientCode.toString()},\n" +
                            "                        ${asmtId.toString()},\n" +
                            "                        ${shiftSelect.toString()},\n" +
                            "                        $empNumber")
                    getAttendanceTime(
                        clientCode.toString(),
                        asmtId.toString(),
                        shiftSelect.toString(),
                        empNumber
                    )
                } else {
                    Log.d("TAGGGGGGGGG", "onResponse: this is -1 case")
                }
            }

            override fun onFailure(call: Call<GeoMappedResponse?>, t: Throwable) {
                CustomToast.showToast(requireContext(), "failed")

            }
        })
    }

    fun getAttendanceTime(clientCode: String, asmtId: String, shift: String, empNumber: String) {

        retrofitInstance.getAttendancDaily("sams", clientCode, asmtId, shift, empNumber)
            .enqueue(object : Callback<DailyAttendanceModel?> {
                override fun onResponse(
                    call: Call<DailyAttendanceModel?>,
                    response: Response<DailyAttendanceModel?>
                ) {


                    intTime = response.body()?.get(0)?.InTime.toString()
                    outTime = response.body()?.get(0)?.OutTime.toString()
                    setShiftJoiningTime(intTime.toString(), outTime.toString())

                    Log.d(
                        "TAGGGGGGGGGG",
                        "onResponse: this is in time $intTime and this is outtime $outTime"
                    )
                }

                override fun onFailure(call: Call<DailyAttendanceModel?>, t: Throwable) {
                    CustomToast.showToast(requireContext(), "failed 2")

                }
            })

    }
}