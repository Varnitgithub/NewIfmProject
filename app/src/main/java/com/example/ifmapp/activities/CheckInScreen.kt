package com.example.ifmapp.activities


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.example.ifmapp.shared_preference.shared_preference_models.CheckOutModel
import com.example.ifmapp.shared_preference.shared_preference_models.CurrentUserShiftsDetails
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.IMEIGetter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import java.nio.file.Files

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
    private var empImageUrl: String? = null
    private var empUserBitmap: Bitmap? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val CAMERA_PERMISSION_CODE = 101
    private lateinit var imeiGetter: IMEIGetter
    private var imageInString: String? = null
    private var employeeName: String? = null
    private var inoutStatus: String? = null
    private var employeeDesignation: String? = null
    private var otp: String? = null
    private var base64Image: String? = null
    private var imagesString: String? = null
    private var imagesBitmap: Bitmap? = null
    private var siteSelect: String? = null
    private var shiftSelect: String? = null
    private var time: String? = null
    private var locationAutoID: String? = null
    private var empNumber: String? = null
    private lateinit var retrofitInstance: ApiInterface
    private lateinit var formattedDate: String
    private var currenTtime: String? = null
    private lateinit var currentDate: Date
    private val CAMERA_REQUEST_CODE = 1
    private val REQUEST_READ_PHONE_STATE = 123

    var inStatus = "IN"
    var outStatus = "OUT"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_check_in_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in_screen)



        binding.progressBar.visibility = View.GONE

        binding.btnRetake.isEnabled = false
        binding.bigProfile.isClickable = false
        binding.cameraImageView.visibility = View.GONE
        binding.allLayout.visibility = View.VISIBLE
        binding.cameraPreviewView.visibility = View.GONE

        inoutStatus = intent.getStringExtra("INOUTStatus")
        mOTP = intent.getStringExtra("mPIN")
        mUserName = intent.getStringExtra("empName")
        Log.d("TAGGGGG", "onCreate: this is motp.........................$mOTP")

        val usersList = SaveUsersInSharedPreference.getList(this@CheckInScreen)

        for (user in usersList) {
            if (user.pin == mOTP && user.userName == mUserName) {
                mUserName = user.userName
                employeeDesignation = user.designation
            }
        }
        imageCapture = ImageCapture.Builder().build()
        retrofitInstance = RetrofitInstance.apiInstance
        imeiGetter = IMEIGetter(this)
        binding.checkinCL.visibility = View.VISIBLE
        binding.finalLayoutCL.visibility = View.GONE
        currentDate = Date()

        formattedDate = getFormattedDate(currentDate)

        cameraExecutor = Executors.newSingleThreadExecutor()


        val usersLists = SaveUsersInSharedPreference.getList(this@CheckInScreen)

        for (user in usersLists){
            if (user.pin==mOTP&&user.userName==mUserName){
                binding.userName.text = user.userName
                binding.designation.text = user.designation

            }
        }

       var size =      SaveUsersInSharedPreference.getCurrentUserShifts(this,mUserName.toString())
            for (i in size){
                if (i.empName== mUserName){

                    otp = i.pin
                    siteSelect = i.site
                    shiftSelect = i.shift
                    empNumber = i.empId
                    binding.userName.text = i.empName
                    binding.designation.text = i.empDesignation
                    locationAutoID = i.locationAutoId
                    Log.d("TAGGGGGGGG", "onCreate: current user.............${i.site}" +
                            " ${i.empId} ${i.empDesignation} $")
                }

            }
        Log.d("TAGGGGGGGG", "onCreate: current user.............$mUserName")

        binding.shifts.text = shiftSelect

        Log.d("TAGGGGGGGG", "onCreate: this is $locationAutoID")
        time = getCurrentTime()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setIntervalMillis(500)
                .build()
        createLocationRequest()

        binding.finalLayoutCL.visibility = View.GONE
        binding.checkinCL.visibility = View.VISIBLE

        getLastLocation()

        binding.btnSubmit.setOnClickListener {

            binding.btnSubmit.setTextColor(resources.getColor(R.color.white))
            binding.btnSubmit.setBackgroundResource(R.drawable.button_back)
            binding.btnRetake.setTextColor(resources.getColor(R.color.check_btn))
            binding.btnRetake.setBackgroundResource(R.drawable.button_backwhite)

            if (base64Image != null && mLatitude != null && mLongitude != null && locationAutoID != null && mAltitude != null) {
                Log.d("TAGGGGGGGG", "onCreate:;llllll $locationAutoID")
                binding.progressBar.visibility = View.VISIBLE
                getGeoMappedSites(
                    "sams",
                    locationAutoID.toString(),
                    mLatitude.toString(),
                    mLongitude.toString()
                )
                binding.btnSubmit.isClickable = false
                AnimationClass.startBlinkAnimation(binding.attendanceMarkedTxt)

                val saveCurrentUserFinalList = ArrayList<CheckOutModel>()
                val saveCurrentUserFinal = CheckOutModel(
                    time.toString(), employeeName.toString(),
                    employeeDesignation.toString(),
                    myaddress.toString(), siteSelect.toString(), shiftSelect.toString()
                )
                saveCurrentUserFinalList.add(saveCurrentUserFinal)

                SaveUsersInSharedPreference.saveCurrentUserFinalCheckout(
                    this@CheckInScreen,
                    saveCurrentUserFinalList
                )


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
                startCamera()
            }
        }


        binding.bigProfile.setOnClickListener {
            if (checkCameraPermission()) {
                startCamera()
                binding.bigProfile.isClickable = false
            } else {
                requestCameraPermission()
            }

        }
        binding.btnCross.setOnClickListener {
            startActivity(Intent(this, DashBoardScreen::class.java))
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
                    getLastLocation()
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
        binding.latlgTxt.text = "$latitude $longitude"
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
    private fun getLastLocation() {
        Log.d("TAGGGG", "getLastLocation: i got location")
        fusedLocationProviderClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                // Got last known location
                location?.let {
                    mLatitude = location.latitude.toString()
                    mLongitude = location.longitude.toString()
                    mAltitude = location.altitude.toString()
                    getAddressFromLocation(this, mLatitude!!.toDouble(), mLongitude!!.toDouble())
                    binding.checkInlatitude.text = mLatitude
                    binding.checkInlongitude.text = mLongitude
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
        cameraExecutor.shutdown()
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


        retrofitInstance.getGeoMappedSites(connectionKey, LocationAutoID, Latitude, Longitude)
            .enqueue(object : Callback<GeoMappedResponse?> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<GeoMappedResponse?>,
                    response: Response<GeoMappedResponse?>
                ) {
                    if (response.isSuccessful
                    ) {
                        if (response.body()?.get(0)?.MessageID.toString()
                                .toInt() == 1
                        ) {

                            Log.d("TAGGGGGGGGGG", "onResponse: response Successfulllll")

                            val emp = response.body()?.get(0)
                            Log.d("TAGGGGGGG", "onResponse: this is $mAltitude")

                            if (myaddress != null) {

                                val imeiPhone = imeiGetter.getIMEI()
                                Log.d(
                                    "TAGGGGG",
                                    "onResponse:1${time} \n2 ${imeiPhone}\n" +
                                            "3${empNumber} 4${emp?.AsmtID} 5${mLatitude.toString()} 6 ${mLongitude.toString()}" +
                                            "7${mAltitude.toString()} 8${
                                                base64Image?.substring(
                                                    1,
                                                    10
                                                )
                                            } 9${LocationAutoID} 10${emp?.ClientCode}" +
                                            "11${siteSelect} 12${shiftSelect} 13${myaddress}"
                                )

                                if (empNumber.toString().isNotEmpty() && emp?.AsmtID.toString()
                                        .isNotEmpty() && inoutStatus.toString().isNotEmpty() &&
                                    time.toString()
                                        .isNotEmpty() && emp?.ClientCode != null && siteSelect != null && myaddress != null

                                ) {
                                    insertAttendance(
                                        "sams",
                                        imeiPhone,
                                        empNumber.toString(),
                                        emp.AsmtID,
                                        empNumber.toString(),
                                        inoutStatus.toString(),
                                        time.toString(),
                                        latitude = mLatitude.toString(),
                                        mLongitude.toString(),
                                        mAltitude.toString(),
                                        base64Image ?: "",
                                        emp.LocationAutoID,
                                        emp.ClientCode, shiftSelect.toString(),
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
                            CustomToast.showToast(
                                this@CheckInScreen,
                                "attendance is already marked "
                            )
                        }
                    } else {
                        Log.d("TAGGGGGGGG", "onResponse: attendance not success")
                    }
                }

                override fun onFailure(
                    call: Call<GeoMappedResponse?>,
                    t: Throwable
                ) {
                    Log.d("TAGGGGGGGGGG", "onFailure: falied")
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
            LocationAutoId, ClientCode, ShiftCode, LocationName
        ).enqueue(object : Callback<AttendanceResponse?> {
            override fun onResponse(
                call: Call<AttendanceResponse?>,
                response: Response<AttendanceResponse?>
            ) {
                if (response.isSuccessful && response.body()?.get(0)?.MessageID?.toInt() == 1) {

                    Log.d("TAGGGGGGGG", "onResponse: attendance inserted")
                    binding.cameraPreviewView.visibility = View.GONE

                    binding.checkinCL.visibility = View.GONE


                    binding.progressBar.visibility = View.VISIBLE
                    binding.attendanceMarkedTxt.text =
                        "Attendance Marked ${inoutStatus} Successfully!"
                    setFinalDialog(
                        mLatitude.toString(),
                        mLongitude.toString(),
                        myaddress.toString()
                    )

                    SaveUsersInSharedPreference.setAttendanceStatus(this@CheckInScreen, "IN")
                    Log.d("TAGGGGGGG", "onResponse: $mOTP is the new otp")
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
                    Log.d("TAGGGGGGG", "onResponse: $mOTP is the new otp")
                    Log.d("TAGGGGGGG", "onResponse: $mUserName is the new otp")
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


    /* @SuppressLint("MissingPermission")
    private fun getDeviceIds(): String? {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        Log.d("TAGGGGGGG", "getDeviceIds: ${telephonyManager.allCellInfo}")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            telephonyManager.deviceId

            null // The IMEI information is restricted on Android 10 and above
        } else {
            // Handling for Android versions below 10
            telephonyManager.deviceId
        }
    }*/


    private fun bitmapToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /* fun onOpenCameraButtonClicked(view: android.view.View) {
         startCamera()
     }*/

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
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

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
                    binding.cameraPreviewView.visibility = View.GONE
                    binding.allLayout.visibility = View.VISIBLE

                    binding.bigProfile.setImageBitmap(imagesBitmap)
                    binding.btnRetake.isEnabled = true

                }

                override fun onError(exception: ImageCaptureException) {
                    CustomToast.showToast(this@CheckInScreen, "Error capturing image")
                }
            }
        )
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
        try {
            // Decode the file into a Bitmap
            var bitmap = BitmapFactory.decodeFile(imageFile.path)

            // Get Exif orientation information
            val exif = android.media.ExifInterface(FileInputStream(imageFile))
            val orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1)

            // Rotate the Bitmap based on the Exif orientation
            val matrix = Matrix()
            when (orientation) {
                3 -> matrix.postRotate(180f)
                6 -> matrix.postRotate(90f)
                8 -> matrix.postRotate(270f)
            }

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

            return bitmap
        } catch (e: Exception) {
            println("Error converting file to Bitmap: ${e.message}")
            return null
        }
    }


    private fun checkLocationPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_REQUEST_CODE
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this@CheckInScreen, MainActivity::class.java)
      //  intent.putExtra("mPIN", otp)
        intent.putExtra("inoutStatus", "OUT")
      //  intent.putExtra("mPIN", mOTP)
      //  intent.putExtra("empName", mUserName)
        Log.d("TAGGGGGGGGGGGGG", "onBackPressed: this is $otp this is $mOTP this is $mUserName")
        startActivity(intent)
    }

    /*  private fun checkPhonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@CheckInScreen,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPhonePermission(){
        ActivityCompat.requestPermissions(
            this@CheckInScreen,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            REQUEST_READ_PHONE_STATE
        )
    }
}*/

}