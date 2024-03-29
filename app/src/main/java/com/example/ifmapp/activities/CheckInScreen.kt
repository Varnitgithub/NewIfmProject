package com.example.ifmapp.activities


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import android.provider.Settings

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.animation.AnimationClass
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivityCheckInScreenBinding
import com.example.ifmapp.modelclasses.attendance_response.AttendanceResponse
import com.example.ifmapp.modelclasses.geomappedsite_model.GeoMappedResponse
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.shared_preference.shared_preference_models.CurrentUserShiftsDetails
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.CheckInternetConnection
import com.example.ifmapp.utils.GlobalLocation
import com.example.ifmapp.utils.IMEIGetter
import com.example.ifmapp.utils.ShiftDetailsObject
import com.example.ifmapp.utils.UserObject
import com.example.ifmapp.utils.UtilModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CheckInScreen : AppCompatActivity() {
    private lateinit var imageCapture: ImageCapture
    private var employeeId: String? = null
    private lateinit var binding: ActivityCheckInScreenBinding
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var myaddress: String? = null
    private var mLatitude: String? = null
    private var mLongitude: String? = null
    private var mOTP: String? = null
    private var mUserName: String? = null
    private val LOCATION_REQUEST_CODE = 111
    private var rotatedBitmap: Bitmap? = null
    private lateinit var photoFile: File
    private lateinit var cameraExecutor: ExecutorService
    private var mAltitude: String? = null
    private val CAMERA_PERMISSION_CODE = 101
    private lateinit var imeiGetter: IMEIGetter
    private var employeeName: String? = null
    private var inoutStatus: String? = null
    private var employeeDesignation: String? = null
    private var otp: String? = null
    private var base64Image: String? = null
    private var imagesBitmap: Bitmap? = null
    private var siteSelect: String? = null
    private var shiftSelect: String? = null
    private var time: String? = null
    private var locationAutoID: String? = null
    private var empNumber: String? = null
    private lateinit var retrofitInstance: ApiInterface
    private lateinit var formattedDate: String
    private var shiftTimingList: String? = null
    private lateinit var currentDate: Date
    private var asmtId: String? = null

    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in_screen)

        binding.progressBar.visibility = View.GONE

        binding.btnRetake.isEnabled = false
        binding.bigProfile.isClickable = false
        binding.cameraImageView.visibility = View.GONE
        binding.allLayout.visibility = View.VISIBLE
        binding.cameraPreviewView.visibility = View.GONE

        inoutStatus = ShiftDetailsObject.inOut
        mOTP = UserObject.userPin
        mUserName = UserObject.userNames
        shiftTimingList = intent.getStringExtra("shiftTimingList")
        shiftSelect = ShiftDetailsObject.shiftSelect
        siteSelect = ShiftDetailsObject.siteSElect
        mAltitude = GlobalLocation.location.altitude
        imageCapture = ImageCapture.Builder().build()
        retrofitInstance = RetrofitInstance.apiInstance
        imeiGetter = IMEIGetter(this)
        binding.checkinCL.visibility = View.VISIBLE
        binding.finalLayoutCL.visibility = View.GONE
        currentDate = Date()

        GlobalLocation.location = UtilModel(
            CheckInternetConnection().GetLocation(this@CheckInScreen)?.latitude.toString(),
            CheckInternetConnection().GetLocation(this@CheckInScreen)?.longitude.toString(),
            CheckInternetConnection().GetLocation(this@CheckInScreen)?.altitude.toString()
        )

        formattedDate = getFormattedDate(currentDate)

        cameraExecutor = Executors.newSingleThreadExecutor()

        Log.d("TAGGGGG", "onCreateofcheckin:site ${ShiftDetailsObject.siteSElect} shift ${ShiftDetailsObject.shiftSelect}" +
                "inout ${ShiftDetailsObject.inOut}")


        val usersLists = SaveUsersInSharedPreference.getList(this@CheckInScreen)

        for (user in usersLists) {
            if (user.pin == mOTP && user.userName == mUserName) {
                binding.userName.text = user.userName
                mUserName = user.userName
                locationAutoID = user.LocationAutoId
                employeeDesignation = user.designation
                binding.designation.text = user.designation
                employeeId = user.empId

            }
        }

        binding.shifts.text = shiftSelect


        time = getCurrentTime()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setIntervalMillis(500)
                .build()
        createLocationRequest()

        binding.finalLayoutCL.visibility = View.GONE
        binding.checkinCL.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            getLastLocation()

        }

        binding.btnSubmit.setOnClickListener {

            binding.btnSubmit.setTextColor(resources.getColor(R.color.white))
            binding.btnSubmit.setBackgroundResource(R.drawable.button_back)
            binding.btnRetake.setTextColor(resources.getColor(R.color.check_btn))
            binding.btnRetake.setBackgroundResource(R.drawable.button_backwhite)
            Log.d("TAGGGGGGGG", "onCreate: .....................id 2")
            Log.d(
                "TAGGGGGGGG",
                "onCreate: .....................id $locationAutoID $mLatitude $mLongitude $mAltitude "
            )
            if (base64Image != null && locationAutoID != null) {
                binding.progressBar.visibility = View.VISIBLE
                Log.d("TAGGGGGGGG", "onCreate: .....................id 3")

                getGeoMappedSites(
                    "sams",
                    locationAutoID.toString(),
                    mLatitude.toString(),
                    mLongitude.toString())
                binding.btnSubmit.isClickable = false
                binding.btnRetake.isClickable = false
                AnimationClass.startBlinkAnimation(binding.attendanceMarkedTxt)
            }
        }
        binding.cameraPreviewView.setOnClickListener {
            takePhoto()
        }
        binding.btnRetake.setOnClickListener {
            if (imagesBitmap != null) {
                binding.btnSubmit.isClickable = true
                binding.btnRetake.setTextColor(resources.getColor(R.color.white))
                binding.btnRetake.setBackgroundResource(R.drawable.button_back)
                binding.btnSubmit.setTextColor(resources.getColor(R.color.check_btn))
                binding.btnSubmit.setBackgroundResource(R.drawable.button_backwhite)
                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        startCamera()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 1000)
            }
        }
        binding.bigProfile.setOnClickListener {
            if (checkCameraPermission()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        startCamera()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 2000) // Delay in milliseconds

                binding.bigProfile.isClickable = false
            } else {
                requestCameraPermission()
            }
        }
        binding.btnCross.setOnClickListener {
            val intent = Intent(this@CheckInScreen, MainActivity::class.java)
            intent.putExtra("inoutStatus", "OUT")
            intent.putExtra("mPIN", mOTP)
            intent.putExtra("empName", mUserName)
            startActivity(intent)
        }
    }
    private fun getAddressFromLocation(
        context: Context,
        latitude: Double,
        longitude: Double
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())

            try {
                val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address: Address = addresses[0]
                    withContext(Dispatchers.Main) {

                        val subThoroughfare = address.subThoroughfare ?: ""
                        val thoroughfare = address.thoroughfare ?: ""
                        val premises = address.premises ?: ""
                        val postalCode = address.postalCode ?: ""
                        val subLocality = address.subLocality ?: ""
                        val subAdminArea = address.subAdminArea ?: ""
                        val locality = address.locality ?: ""
                        val adminArea = address.adminArea ?: ""

                        val formattedAdress = buildString {
                            if (premises.isNotEmpty()) append("$premises ")
                            if (subThoroughfare.isNotEmpty()) append("$subThoroughfare ")
                            if (thoroughfare.isNotEmpty()) append("$thoroughfare ")
                            if (postalCode.isNotEmpty()) append("$postalCode ")
                            if (subLocality.isNotEmpty()) append("$subLocality ")
                            if (subAdminArea.isNotEmpty()) append("$subAdminArea ")
                            if (locality.isNotEmpty()) append("$locality ")
                            if (adminArea.isNotEmpty()) append("$adminArea ")
                        }

                        binding.locationName.text = formattedAdress
                        binding.address.text = formattedAdress
                        myaddress = formattedAdress

                        address.apply {
                            Log.d(
                                "TAGGGGGGGG", "getAddressFromLocation: $adminArea $subAdminArea" +
                                        " $locality $subLocality $countryCode $countryName $extras $featureName $locale $maxAddressLineIndex " +
                                        "$postalCode $subThoroughfare $thoroughfare $premises"
                            )
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e("Geocoding", "Error getting address from location: $e")
            }
        }
    }


    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                    binding.bigProfile.isClickable = false
                    /*val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)*/
                } else {
                    // Permission denied, show a message to the user
                    CustomToast.showToast(this@CheckInScreen, "Please Allow Camera permission")
                }
            }

            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    CustomToast.showToast(this@CheckInScreen, "Please Allow Location permission")
                }
            }

        }

    }

    private fun setFinalDialog(

        latitude: String,
        longitude: String,
        address: String
    ) {
        binding.progressBar.visibility = View.GONE
        binding.checkinCL.visibility = View.GONE

        binding.finalLayoutCL.visibility = View.VISIBLE
        binding.nameAtfinal.text = mUserName
        binding.designationAtfinal.text = employeeDesignation
        binding.shiftAtfinal.text = "${siteSelect} $shiftSelect $formattedDate"
        binding.latlgTxt.text =
            "${GlobalLocation.location.latitude} ${GlobalLocation.location.longitude}"
        binding.address.text = myaddress
        binding.profileFinal.setImageBitmap(imagesBitmap)


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
    suspend fun getLastLocation() {
        fusedLocationProviderClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                // Got last known location
                location?.let {
                    GlobalLocation.location = UtilModel(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        location.altitude.toString()
                    )
                    getAddressFromLocation(
                        this,
                        GlobalLocation.location.latitude.toDouble(),
                        GlobalLocation.location.longitude.toDouble()
                    )
                    Log.d("GLOBAL", "checkin: ${GlobalLocation.location}")

                    binding.checkInlatitude.text = location.latitude.toString()
                    binding.checkInlongitude.text = location.longitude.toString()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
        cameraExecutor.shutdown()
       closeCamera()
    }
    private fun closeCamera() {
        cameraCaptureSession?.close()
        cameraDevice?.close()

        cameraCaptureSession = null
        cameraDevice = null
    }

    private fun getFormattedDate(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
        val dayOfMonth = SimpleDateFormat("d", Locale.getDefault()).format(date)
        val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date)
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)

        return "$dayOfWeek $dayOfMonth $month $year"
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getGeoMappedSites(
        connectionKey: String,
        LocationAutoID: String,
        Latitude: String,
        Longitude: String
    ) {


        retrofitInstance.getGeoMappedSites(
            connectionKey,
            UserObject.userId,
            LocationAutoID,

        )
            .enqueue(object : Callback<GeoMappedResponse?> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<GeoMappedResponse?>,
                    response: Response<GeoMappedResponse?>
                ) {
                    if (response.isSuccessful
                    ) {

                            val emp = response.body()?.get(0)

                            if (myaddress != null) {
                                asmtId = response.body()?.get(0)?.AsmtId
                                val imeiPhone = "no imei"
                                Log.d(
                                    "TAGGGGG",
                                    "onResponse:1 ${time} \n2 ${imeiPhone}\n" +
                                            "3 ${employeeId} 4 ${emp?.AsmtId} 5 ${mLatitude.toString()} 6 ${mLongitude.toString()}" +
                                            "7 ${mAltitude.toString()} 8 ${
                                                base64Image?.substring(
                                                    1,
                                                    10
                                                )
                                            } 9 ${LocationAutoID} 10 ${emp?.ClientCode}" +
                                            "11 ${siteSelect} 12 ${shiftSelect} 13 ${myaddress}"
                                )

                                if (employeeId.toString().isNotEmpty() && emp?.AsmtId.toString()
                                        .isNotEmpty() && inoutStatus.toString().isNotEmpty() &&
                                    time.toString()
                                        .isNotEmpty() && emp?.ClientCode != null && siteSelect != null && myaddress != null

                                ) {
                                    val deviceId = getUniqueDeviceId()

                                    insertAttendance(
                                        "sams",
                                        deviceId,
                                        employeeId.toString(),
                                        emp.AsmtId,
                                        employeeId.toString(),
                                        ShiftDetailsObject.inOut,
                                        time.toString(),
                                        GlobalLocation.location.latitude,
                                        GlobalLocation.location.longitude,
                                        GlobalLocation.location.altitude,
                                        base64Image ?: "",
                                        UserObject.locationAutoId,
                                        ShiftDetailsObject.siteSElect, ShiftDetailsObject.shiftSelect,
                                        myaddress.toString()
                                    )
                                } else {
                                    CustomToast.showToast(
                                        this@CheckInScreen,
                                        "Some details are mission"
                                    )
                                }
                            }


                    } else {
                    }
                }

                override fun onFailure(
                    call: Call<GeoMappedResponse?>,
                    t: Throwable
                ) {
                }
            })

    }


    fun insertAttendance(
        connectionKey: String,
        IMEI: String?,
        userId: String,
        AsmtID: String,
        employeeNumber: String,
        InOutStatus: String,
        DutyDateTime: String,
        latitude: String,
        longitude: String,
        altitude: String,
        employeeImageBase64: String,
        LocationAutoId: String,
        ClientCode: String,
        ShiftCode: String,
        LocationName: String
    ) {

        retrofitInstance.insertAttendance(
            connectionKey, IMEI ?: "", userId, AsmtID, employeeNumber, InOutStatus,
            DutyDateTime, latitude, longitude, altitude, employeeImageBase64,
            LocationAutoId, ClientCode, ShiftCode, LocationName,ShiftDetailsObject.post
        ).enqueue(object : Callback<AttendanceResponse?> {
            override fun onResponse(
                call: Call<AttendanceResponse?>,
                response: Response<AttendanceResponse?>
            ) {
                if (response.isSuccessful) {

                    if (response.body()?.get(0)?.MessageID?.toInt() == 1) {
                        Log.d("TAGGGGGGGG", "onResponse: ${response.body()?.get(0)?.MessageString}")
                        Log.d("TAGGGGGGGG", "onResponse: ${response.body()?.get(0)?.MessageID}")
                        Log.d("TAGGGGGGGG", "onResponse: attendance inserted")
                        binding.cameraPreviewView.visibility = View.GONE

                        binding.checkinCL.visibility = View.GONE


                        binding.progressBar.visibility = View.VISIBLE
                        binding.attendanceMarkedTxt.text =
                            "Attendance Marked ${inoutStatus} Successfully!"
                        saveUserIntoShaPref()
                        if (inoutStatus == "OUT") {
                            SaveUsersInSharedPreference.deleteCurrentUserShifts(
                                this@CheckInScreen,
                                userId
                            )
                            Log.d(
                                "TAGGGGGGGGG",
                                "insertAttendance: data delete from checkin successfully"
                            )
                        }
                        setFinalDialog(
                            mLatitude.toString(),
                            mLongitude.toString(),
                            myaddress.toString()
                        )

                        SaveUsersInSharedPreference.setAttendanceStatus(this@CheckInScreen, "IN")
                        val delayMillis = 5000L
                        Handler().postDelayed({
                            finish()
                            val intent = Intent(this@CheckInScreen, MainActivity::class.java)
                            intent.putExtra("inoutStatus", "OUT")
                            intent.putExtra("mPIN", mOTP)
                            intent.putExtra("empName", mUserName)
                            startActivity(intent)
                        }, delayMillis)
                    } else {
                        CustomToast.showToast(
                            this@CheckInScreen,
                            response.body()?.get(0)?.MessageString.toString()
                        )
                        binding.progressBar.visibility = View.GONE

                    }
                } else {
                    CustomToast.showToast(
                        this@CheckInScreen,
                        response.body()?.get(0)?.MessageString.toString()
                    )
                    binding.progressBar.visibility = View.GONE
                    val delayMillis = 5000L
                    Handler().postDelayed({
                        finish()
                        val intent = Intent(this@CheckInScreen, MainActivity::class.java)
                        intent.putExtra("inoutStatus", "OUT")
                        //   intent.putExtra("mPIN", mOTP)
                        //    intent.putExtra("empName", mUserName)
                        startActivity(intent)
                    }, delayMillis)
                }
            }

            override fun onFailure(call: Call<AttendanceResponse?>, t: Throwable) {
                Log.d("TAGGGGGGGGGGG", "onFailure: attendance failed")
            }
        })

    }

    private fun startCamera() {

        binding.cameraImageView.visibility = View.GONE
        binding.allLayout.visibility = View.GONE
        binding.cameraPreviewView.visibility = View.VISIBLE
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Create a preview use case
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreviewView.surfaceProvider)
                }

            // Set up the image capture use case
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Set up the camera selector to use the front camera
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()

            try {
                // Unbind any existing camera use cases before rebinding
                cameraProvider.unbindAll()

                // Bind the use cases to the camera
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                CustomToast.showToast(this@CheckInScreen, "Unable to open camera")
            }

        }, ContextCompat.getMainExecutor(this))

    }

    private fun takePhoto() {
        // Create a timestamped file to save the image
        val photoFileName =
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}"
        val storageDirectory = getExternalFilesDir(null)
        photoFile = File.createTempFile(photoFileName, ".jpg", storageDirectory)

        // Create output options object to configure ImageCapture
        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Capture the image
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Image is saved, now process the image
                    base64Image = imageToBase64(photoFile)
                    imagesBitmap = fileToBitmap(photoFile)
                    rotatedBitmap = rotateBitmap(imagesBitmap!!, 270f)
                    binding.cameraPreviewView.visibility = View.GONE
                    binding.allLayout.visibility = View.VISIBLE
                    //imagesBitmap = rotateBitmap(imagesBitmap!!, 180f)
                    binding.bigProfile.setImageBitmap(rotatedBitmap)
                    binding.btnRetake.isEnabled = true

                }

                override fun onError(exception: ImageCaptureException) {
                    CustomToast.showToast(this@CheckInScreen, "Error capturing image")
                }
            }
        )
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun imageToBase64(imageFile: File): String? {
        try {
            val imageBytes = Files.readAllBytes(imageFile.toPath())
            val base64Encoded = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            return base64Encoded
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return null
        }
    }

    fun fileToBitmap(imageFile: File): Bitmap? {
        return try {
            BitmapFactory.decodeFile(imageFile.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun checkLocationPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_REQUEST_CODE
        )
    }

    fun saveUserIntoShaPref() {
        val currentUserShiftList = ArrayList<CurrentUserShiftsDetails>()
        val currentUserShift = CurrentUserShiftsDetails(
            shiftSelect.toString(),
            siteSelect.toString(),
            employeeId.toString(), asmtId.toString()
        )

        currentUserShiftList.add(currentUserShift)
        SaveUsersInSharedPreference.saveCurrentUserShifts(
            this@CheckInScreen,
            currentUserShiftList, employeeId.toString()
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this@CheckInScreen, MainActivity::class.java)
        intent.putExtra("mPIN", otp)
        intent.putExtra("inoutStatus", "OUT")
        intent.putExtra("mPIN", mOTP)
        intent.putExtra("empName", mUserName)
        startActivity(intent)
    }

    private fun getCurrentFormattedDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEE d MMM", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    fun getUniqueDeviceId(): String {
        return Settings.Secure.getString(
            this@CheckInScreen.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }



}