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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.activities.CheckInScreen
import com.example.ifmapp.activities.DashBoardScreen
import com.example.ifmapp.activities.ScannerScreen
import com.example.ifmapp.adapters.AddAccountAdapter
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.FragmentHomeBinding
import com.example.ifmapp.modelclasses.InOutTimeModel
import com.example.ifmapp.modelclasses.geomappedsite_model.GeoMappedResponse
import com.example.ifmapp.modelclasses.postmodel.PostModel
import com.example.ifmapp.modelclasses.postmodel.PostModelItem
import com.example.ifmapp.modelclasses.shiftwithtime_model.ShiftWithTimeResponse
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
import com.example.ifmapp.modelclasses.verifymobile.VerifyOtpResponse
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.CheckInternetConnection
import com.example.ifmapp.utils.GlobalLocation
import com.example.ifmapp.utils.ShiftDetailsObject
import com.example.ifmapp.utils.UserObject
import com.example.ifmapp.utils.UtilModel
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment(
    private var context: Context,
    private var otp: String,
    private var empNumber: String,
    private var userName: String
) : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var retrofitInstance: ApiInterface = RetrofitInstance.apiInstance
    private lateinit var addAccountAdapter: AddAccountAdapter
    private lateinit var shiftListInPref: ArrayList<String>
    private lateinit var siteListInPref: ArrayList<String>
    private lateinit var shiftList: ArrayList<String>
    private lateinit var siteList: ArrayList<String>
    private lateinit var postList: ArrayList<PostModelItem>
    private lateinit var InOuttimeList: ArrayList<InOutTimeModel>
    private lateinit var siteNamesList: ArrayList<Pair<String, String>>
    private lateinit var siteAsmtList: ArrayList<Pair<String, String>>
    private lateinit var shiftListTiming: ArrayList<String>
    private var shiftSelect: String? = ""
    private var postSelect: String? = ""
    private var shiftSelectFromSF: String? = null
    private var siteSelect: String? = ""
    private lateinit var currentShiftActualLiveData: MutableLiveData<String>
    private lateinit var currentShiftServerLiveData: MutableLiveData<InOutTimeModel>
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var mLatitude: String? = null
    private var mLongitude: String? = null
    private var mAltitude: String? = null
    private var currentUser: UserListModel? = null
    private var empDesignation: String? = null
    private var asmtId: String? = null
    private var empName: String? = null
    private var locationAutoId: String? = null
    private var intTime: String? = null
    private var outTime: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        // addAccountAdapter = AddAccountAdapter(requireContext(), this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)



        currentUser = SaveUsersInSharedPreference.getUserByPin(requireContext(), otp, userName)


        locationAutoId = currentUser?.LocationAutoId.toString()
        empName = currentUser?.userName
        UserObject.userNames = currentUser?.userName.toString()
        UserObject.userId = currentUser?.empId.toString()
        UserObject.userPin = currentUser?.pin.toString()
        UserObject.designation = currentUser?.designation.toString()
        UserObject.locationAutoId = currentUser?.LocationAutoId.toString()

        empNumber = currentUser?.empId.toString()
        empDesignation = currentUser?.designation
        val allusers = SaveUsersInSharedPreference.getList(requireContext())

        binding.userName.text = currentUser?.userName
        binding.userId.text = empNumber
        binding.designation.text = currentUser?.designation

        binding.shiftStartdateText.text = getFormattedDate()
        binding.shiftEnddateText.text = getFormattedDate()
        binding.join.text = getFormattedDate()
        binding.out.text = getFormattedDate()


        GlobalLocation.location = UtilModel(
            CheckInternetConnection().GetLocation(requireContext())?.latitude.toString(),
            CheckInternetConnection().GetLocation(requireContext())?.longitude.toString(),
            CheckInternetConnection().GetLocation(requireContext())?.altitude.toString()
        )

        createLocationRequest()
        binding.checkoutBtn.isEnabled = false
        retrofitInstance = RetrofitInstance.apiInstance

        binding.btnLogout.setOnClickListener {

            startActivity(Intent(requireContext(), DashBoardScreen::class.java))
        }
        shiftListTiming = ArrayList()
        siteListInPref = ArrayList()
        shiftListInPref = ArrayList()
        siteList = ArrayList()
        InOuttimeList = ArrayList()
        shiftList = ArrayList()
        postList = ArrayList()
        siteNamesList = ArrayList()
        siteAsmtList = ArrayList()
        currentShiftActualLiveData = MutableLiveData()
        currentShiftServerLiveData = MutableLiveData()

        CoroutineScope(Dispatchers.IO).launch {
            getLastLocation()

        }

        binding.checkInBtn.setOnClickListener {


            Log.d(
                "TAAAAAAAAAA",
                "onCreateView:outside $siteSelect $shiftSelect $postList ...................."
            )
            if (siteSelect!!.isNotEmpty() && shiftSelect!!.isNotEmpty() && postList.size == 1) {

                binding.checkoutBtn.setTextColor(resources.getColor(R.color.check_btn))
                binding.checkoutBtn.setBackgroundResource(R.drawable.button_backwhite)
                binding.checkInBtn.setTextColor(resources.getColor(R.color.white))
                binding.checkInBtn.setBackgroundResource(R.drawable.button_back)

                ShiftDetailsObject.inOut = "IN"
                ShiftDetailsObject.shiftSelect = shiftSelect.toString()
                ShiftDetailsObject.siteSElect = siteSelect.toString()
                ShiftDetailsObject.postSelect = ""
                ShiftDetailsObject.post = postList.get(0).PostAutoId

                checkPostWithSite(postList.get(0).PostAutoId)


            } else if (siteSelect!!.isNotEmpty() && shiftSelect!!.isNotEmpty() && postList.size > 1) {

                ShiftDetailsObject.inOut = "IN"
                ShiftDetailsObject.shiftSelect = shiftSelect.toString()
                ShiftDetailsObject.siteSElect = siteSelect.toString()
                ShiftDetailsObject.postSelect = ""


                binding.checkoutBtn.setTextColor(resources.getColor(R.color.check_btn))
                binding.checkoutBtn.setBackgroundResource(R.drawable.button_backwhite)
                binding.checkInBtn.setTextColor(resources.getColor(R.color.white))
                binding.checkInBtn.setBackgroundResource(R.drawable.button_back)
                val intent = Intent(requireContext(), ScannerScreen::class.java)
                intent.putExtra("INOUTStatus", "IN")
                intent.putExtra("mPIN", otp)
                intent.putExtra("empName", userName)
                intent.putExtra("shiftSelect", shiftSelect)
                intent.putExtra("siteSelect", siteSelect)

                startActivity(intent)
            } else {
                var c = siteSelect
                CustomToast.showToast(requireContext(), "Please select Shift and Site")

            }
        }
        binding.checkoutBtn.setOnClickListener {
            binding.checkoutBtn.isEnabled = false
            binding.checkInBtn.setTextColor(resources.getColor(R.color.check_btn))
            binding.checkInBtn.setBackgroundResource(R.drawable.button_backwhite)
            binding.checkoutBtn.setTextColor(resources.getColor(R.color.white))
            binding.checkoutBtn.setBackgroundResource(R.drawable.button_back)

            ShiftDetailsObject.inOut = "OUT"
            ShiftDetailsObject.shiftSelect = shiftSelect.toString()
            ShiftDetailsObject.siteSElect = siteSelect.toString()
            ShiftDetailsObject.postSelect = ""

            var intent = Intent(requireContext(), CheckInScreen::class.java)
            intent.putExtra("INOUTStatus", "OUT")
            intent.putExtra("mPIN", otp)
            intent.putExtra("empName", userName)
            intent.putExtra("shiftSelect", shiftSelect)
            intent.putExtra("siteSelect", siteSelect)
            startActivity(intent)
        }

        return binding.root
    }


    @SuppressLint("MissingPermission")
    fun createLocationRequest() {
        try {
            fusedLocationProviderClient?.requestLocationUpdates(
                LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 5000 // Update interval in milliseconds
                    fastestInterval = 3000 // Fastest update interval in milliseconds
                }, locationCallback!!, null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastLocation() {
        fusedLocationProviderClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                // Got last known location
                location?.let {
                    /*   GlobalLocation.location = UtilModel(
                           location.latitude.toString(),
                           location.longitude.toString(),
                           location.altitude.toString()
                       )*/
                }
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

    /*  @SuppressLint("MissingPermission")
      override fun onclick(employeeModel: UserListModel, position: Int) {

      }*/

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

    private fun setAmPm(time: String, joinOut: TextView) {
        if (time.isNotEmpty()) {
            if (time.substring(0, 2).toInt() < 12) {
                joinOut.text = "AM"
            } else {
                joinOut.text = "PM"
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d("GLOBAL", "home: ${GlobalLocation.location}")
        binding.progressBar.visibility = View.VISIBLE

        currentShiftActualLiveData.observe(requireActivity()) {
            for (timing in shiftListTiming) {
                if (timing.substring(0, 1) == it) {
                    binding.shiftStartTime.text = timing.substring(4, 9)
                    setAmPm(timing.substring(4, 9), binding.startAm)
                    binding.shiftEndTime.text = timing.substring(10, 15)
                    setAmPm(timing.substring(4, 9), binding.endPm)
                }
            }
            for (time in InOuttimeList) {
                if (it == time.shift) {
                    if (time.inTime.isEmpty() && time.outTime.isEmpty()) {
                        userNoExists(time.inTime, time.outTime)
                    } else if (time.inTime.isNotEmpty() && time.outTime.isEmpty()) {
                        userInExists(time.inTime, time.outTime)

                    } else {
                        userInOutExists(
                            time.inTime,
                            time.outTime,
                            locationAutoId.toString(),
                            empNumber
                        )
                    }
                }
            }
        }
        Log.d(
            "TAGGGGGGG",
            "onResume: this is user details ${UserObject.userNames} ${UserObject.userId} ${UserObject.designation} ${UserObject.userPin}"
        )
        binding.userName.text = UserObject.userNames
        binding.userId.text = UserObject.userId

        binding.designation.text = UserObject.designation
        //site list called
        getSitesFromServer(
            UserObject.locationAutoId,
            empNumber
        )
    }

    fun setShiftSelection(
        locationAutoid: String,
        userId: String,
        asmtId: String,
        shiftList: ArrayList<String>
    ) {
        var _SiteList = ArrayList<String>()
        _SiteList.addAll(shiftList)
        val adapterSelectionShift = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            ArrayList<String>()
        )
        for (site in SaveUsersInSharedPreference.getCurrentUserShifts(requireActivity(), userId)) {
            if (shiftList.contains(site.shiftDetails.substring(0, 1))) {
                _SiteList.clear()
                _SiteList.add(site.shiftDetails.substring(0, 1))
            }
        }
        adapterSelectionShift.addAll(_SiteList)

        adapterSelectionShift.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectShift.adapter = adapterSelectionShift

        binding.spinnerSelectShift.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position)
                    if (selectedItem != null) {
                        currentShiftActualLiveData.postValue(selectedItem.toString())
                        shiftSelect = selectedItem.toString()
                    } else {
                        shiftSelect = ""
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parent?.let {
                        if (it.count > 0) {
                        }
                    }
                }
            }
    }

    fun setSiteSelection(
        locationAutoid: String,
        userId: String,
        asmtId: String,
        sites: ArrayList<String>
    ) {
        val _SiteList = ArrayList<String>()
        _SiteList.clear()
        _SiteList.addAll(sites)

        val adapterSelectionShift = ArrayAdapter(
            context,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            ArrayList<String>()
        )

        for (site in SaveUsersInSharedPreference.getCurrentUserShifts(requireActivity(), userId)) {
            if (sites.contains(site.site)) {
                _SiteList.clear()
                _SiteList.add(site.site)
            }
        }
        adapterSelectionShift.addAll(_SiteList)

        adapterSelectionShift.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectSite.adapter = adapterSelectionShift

        binding.spinnerSelectSite.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SuspiciousIndentation")
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position)
                    if (selectedItem != null) {

                        val mSelectedSite = getClientCodeBySiteName(selectedItem.toString())
                        val masmt = getClientasmtBySiteName(selectedItem.toString())

                        ShiftDetailsObject.asmtId = masmt!!

                        siteSelect = mSelectedSite
                        getShiftsFromServer(
                            locationAutoId.toString(),
                            userId,
                            mSelectedSite!!,
                            asmtId
                        )
                        getPosts(locationAutoid, mSelectedSite, asmtId)


                        Log.d("TAGGGGG", "onItemSelected: this condition $mSelectedSite")
                    } else {
                        Log.d("TAGGGGG", "onItemSelected: this condition null")

                        siteSelect == ""
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    parent?.let {
                        if (it.count > 0) {

                        }
                    }
                }
            }
    }

    // Function to retrieve shifts from SharedPreferences
    fun getClientCodeBySiteName(clientSiteName: String): String? {
        for (pair in siteNamesList) {
            if (pair.first == clientSiteName) {
                return pair.second // Return ClientCode if ClientSiteName matches
            }
        }
        return null // Return null if no match is found
    }

    fun getClientasmtBySiteName(clientSiteName: String): String? {
        for (pair in siteAsmtList) {
            if (pair.first == clientSiteName) {
                return pair.second // Return ClientCode if ClientSiteName matches
            }
        }
        return null // Return null if no match is found
    }

    private fun getSitesFromServer(
        locationAutoid: String,
        userId: String
    ) {
        if (locationAutoid.isNotEmpty() && userId.isNotEmpty()) {
            siteList.clear()

            retrofitInstance.getGeoMappedSites(
                "sams", userId, locationAutoid
            ).enqueue(object : Callback<GeoMappedResponse?> {
                override fun onResponse(
                    call: Call<GeoMappedResponse?>,
                    response: Response<GeoMappedResponse?>
                ) {
                    if (response.isSuccessful) {
                        Log.d("TZZZZZZZZZ", "onResponse: this is here 2")


                        if (response.body()!![0].MessageID.toInt() == 1) {
                            val sizes = response.body()?.size?.minus(1)
                            for (i in 0..sizes!!) {
                                siteList.add(response.body()!!.get(i).ClientSiteName)
                                siteNamesList.add(
                                    response.body()?.get(i)!!.ClientSiteName to response.body()
                                        ?.get(i)!!.ClientCode
                                )
                                siteAsmtList.add(
                                    response.body()?.get(i)!!.ClientSiteName to response.body()
                                        ?.get(i)!!.AsmtId
                                )
                            }
                            Log.d("TZZZZZZZZZ", "onResponse: this is sitelist $siteList")
                            Log.d("TZZZZZZZZZ", "onResponse: this is sitelist $siteNamesList")

                            asmtId = response.body()?.get(0)?.AsmtId.toString()
                            Log.d(
                                "TAGGGGGGGGGGGGG",
                                "onResponse: this is site list..............$siteList"
                            )
                            setSiteSelection(locationAutoid, userId, asmtId.toString(), siteList)
                        } else {
                            CustomToast.showToast(
                                requireContext(),
                                "Employee Site mapping not exists!!"
                            )
                            binding.progressBar.visibility = View.GONE
                            startActivity(Intent(requireContext(), DashBoardScreen::class.java))
                        }
                    }
                }

                override fun onFailure(
                    call: Call<GeoMappedResponse?>,
                    t: Throwable
                ) {

                }
            })
        }

    }

    private fun getShiftsFromServer(
        locationAutoid: String,
        userId: String,
        siteSelect: String,
        asmtId: String
    ) {
        Log.d(
            "TAGGGGGGGGGGGG",
            "getShiftsFromServer: $locationAutoid ${UserObject.userId} $siteSelect ${ShiftDetailsObject.asmtId}"
        )
        if (siteSelect.isNotEmpty() && userId.isNotEmpty() && locationAutoid.isNotEmpty() && asmtId.isNotEmpty()) {
            retrofitInstance.getAttendancDailyWithShift(
                "sams",
                siteSelect, ShiftDetailsObject.asmtId,
                locationAutoid, UserObject.userId
            ).enqueue(object : Callback<ShiftWithTimeResponse?> {
                override fun onResponse(
                    call: Call<ShiftWithTimeResponse?>,
                    response: Response<ShiftWithTimeResponse?>
                ) {
                    if (response.isSuccessful) {

                        var size = response.body()?.size
                        for (shifts in response.body()!!) {
                            Log.d("TAGGGGGGGGGGGG", "getShiftsFromServer:${shifts.ShiftDetails}")

                            shiftList.add(shifts.ShiftCode)
                            InOuttimeList.add(
                                InOutTimeModel(
                                    shifts.InTime,
                                    shifts.OutTime,
                                    shifts.ShiftCode
                                )
                            )
                            shiftListTiming.add(shifts.ShiftDetails)

                        }
                        setShiftSelection(locationAutoid, userId, asmtId, shiftList)
                        shiftList.clear()

                    } else {
                        CustomToast.showToast(requireContext(), "Shifts Not Found")
                    }
                }

                override fun onFailure(call: Call<ShiftWithTimeResponse?>, t: Throwable) {

                }
            })
        }


    }

    private fun userNoExists(inTime: String, outTime: String) {
        binding.joiningTime.text = inTime
        binding.outTime.text = outTime
        binding.checkInBtn.isClickable = true
        binding.checkInBtn.isEnabled = true
        binding.checkoutBtn.isEnabled = false
        binding.checkoutBtn.isClickable = false
        binding.checkoutBtn.setTextColor(resources.getColor(R.color.check_btn))
        binding.checkoutBtn.setBackgroundResource(R.drawable.button_backwhite)
        binding.checkInBtn.setTextColor(resources.getColor(R.color.white))
        binding.checkInBtn.setBackgroundResource(R.drawable.button_back)
        binding.progressBar.visibility = View.GONE

    }

    private fun userInExists(inTime: String, outTime: String) {

        binding.joiningTime.text = inTime
        setAmPm(inTime, binding.joiningAm)
        binding.outTime.text = outTime
        binding.outPm.text = ""
        binding.checkInBtn.isClickable = false
        binding.checkInBtn.isEnabled = false
        binding.checkoutBtn.isEnabled = true
        binding.checkoutBtn.isClickable = true
        binding.checkInBtn.setTextColor(resources.getColor(R.color.check_btn))
        binding.checkInBtn.setBackgroundResource(R.drawable.button_backwhite)
        binding.checkoutBtn.setTextColor(resources.getColor(R.color.white))
        binding.checkoutBtn.setBackgroundResource(R.drawable.button_back)
        binding.progressBar.visibility = View.GONE


    }

    private fun userInOutExists(
        inTime: String,
        outTime: String,
        locationAutoid: String,
        userId: String,
    ) {
        binding.joiningTime.text = inTime
        setAmPm(inTime, binding.joiningAm)
        binding.outTime.text = outTime
        setAmPm(outTime, binding.outPm)
        binding.checkInBtn.isClickable = false
        binding.checkInBtn.isEnabled = false
        binding.checkoutBtn.isEnabled = false
        binding.checkoutBtn.isClickable = false
        binding.joiningAm.text = ""
        binding.outPm.text = ""
        binding.checkInBtn.setTextColor(resources.getColor(R.color.check_btn))
        binding.checkInBtn.setBackgroundResource(R.drawable.button_backwhite)
        binding.checkoutBtn.setTextColor(resources.getColor(R.color.check_btn))
        binding.checkoutBtn.setBackgroundResource(R.drawable.button_backwhite)
        binding.progressBar.visibility = View.GONE
        //shiftList.clear()
        //siteList.clear()
        // getSitesFromServer(locationAutoid, "28.4063024", "77.0685936", userId)


    }

    fun getPosts(locationAutoid: String, siteSelect: String, asmtId: String) {
        retrofitInstance.getSitesPost("sams", locationAutoid, siteSelect, asmtId)
            .enqueue(object : Callback<PostModel?> {
                override fun onResponse(
                    call: Call<PostModel?>,
                    response: Response<PostModel?>
                ) {
                    if (response.isSuccessful) {

                        for (i in 0 until response.body()!!.size) {
                            postList.add(response.body()!!.get(i))
                        }
                    } else {
                        CustomToast.showToast(requireContext(), "No Posts Available!!")
                    }
                }

                override fun onFailure(call: Call<PostModel?>, t: Throwable) {

                }
            })
    }

    private fun checkPostWithSite(postId: String) {
        Log.d("SITE", "checkPostWithSite: ${ShiftDetailsObject.siteSElect}")
        Log.d("SITE", "checkPostWithSite: ${ShiftDetailsObject.asmtId}")
        retrofitInstance.getGeoMappedSitesBasisOfPost(
            "sams",
            UserObject.locationAutoId,
            GlobalLocation.location.latitude,
            GlobalLocation.location.longitude,
            ShiftDetailsObject.siteSElect,
            ShiftDetailsObject.asmtId,
            postId
        )
            .enqueue(object : Callback<VerifyOtpResponse?> {
                override fun onResponse(
                    call: Call<VerifyOtpResponse?>,
                    response: Response<VerifyOtpResponse?>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.get(0)!!.MessageID.toInt() == 1) {
                            ShiftDetailsObject.post = postId
                            val intent = Intent(requireContext(), CheckInScreen::class.java)
                            intent.putExtra("INOUTStatus", "IN")
                            intent.putExtra("mPIN", otp)
                            intent.putExtra("empName", userName)
                            intent.putExtra("shiftSelect", shiftSelect.toString())
                            intent.putExtra("siteSelect", siteSelect.toString())
                            intent.putExtra("shiftTimingList", shiftListTiming)
                            startActivity(intent)

                        } else {
                            CustomToast.showToast(
                                requireContext(),
                                response.body()?.get(0)!!.MessageString
                            )
                        }
                    } else {
                        CustomToast.showToast(requireContext(), "Response Unsuccessful")
                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponse?>, t: Throwable) {

                }
            })


    }

}



