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
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
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
import java.nio.file.Paths

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_check_in_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in_screen)

        binding.btnRetake.isEnabled = false
        binding.bigProfile.isClickable = false
        binding.cameraImageView.visibility = View.GONE
        binding.allLayout.visibility = View.VISIBLE
        binding.cameraPreviewView.visibility = View.GONE


        imageCapture = ImageCapture.Builder().build()
        retrofitInstance = RetrofitInstance.apiInstance
        imeiGetter = IMEIGetter(this)
        binding.checkinCL.visibility = View.VISIBLE
        binding.finalLayoutCL.visibility = View.GONE
        currentDate = Date()

        formattedDate = getFormattedDate(currentDate)

        cameraExecutor = Executors.newSingleThreadExecutor()

        val currentUser: CurrentUserShiftsDetails =
            SaveUsersInSharedPreference.getCurrentUserShifts(this)[0]

        otp = currentUser.pin
        siteSelect = currentUser.site
        shiftSelect = currentUser.shift
        empNumber = currentUser.empId
        binding.userName.text = currentUser.empName
        binding.designation.text = currentUser.empDesignation
        binding.shifts.text = shiftSelect
        locationAutoID = currentUser.locationAutoId


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

            if (base64Image != null && mLatitude != null && mLongitude != null && locationAutoID != null) {
                Log.d("TAGGGGGGGG", "onCreate:;llllll $locationAutoID")
                getGeoMappedSites(
                    "sams",
                    locationAutoID.toString(),
                    mLatitude.toString(),
                    mLongitude.toString()
                )
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

                binding.btnRetake.setTextColor(resources.getColor(R.color.white))
                binding.btnRetake.setBackgroundResource(R.drawable.button_back)
                binding.btnSubmit.setTextColor(resources.getColor(R.color.check_btn))
                binding.btnSubmit.setBackgroundResource(R.drawable.button_backwhite)
                startCamera()
            }
        }
        val imeei = getIMEI(this)

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

                        if (address.subThoroughfare != null && address.thoroughfare != null &&address.premises!=null
                            && address.postalCode!=null && address.subLocality!=null
                            && address.subAdminArea!=null &&address.locality!=null&&  address.adminArea!=null
                            && address.subThoroughfare.isNotEmpty() && address.thoroughfare.isNotEmpty() &&address.premises.isNotEmpty()
                            && address.postalCode.isNotEmpty() && address.subLocality.isNotEmpty()
                            && address.subAdminArea.isNotEmpty() &&address.locality.isNotEmpty() &&  address.adminArea.isNotEmpty()) {
                            val formattedAdress =
                                "${address.premises} ${address.subThoroughfare} ${address.thoroughfare} ${address.postalCode} ${address.subLocality} ${address.subAdminArea} ${address.locality}  ${address.adminArea} "
                            binding.locationName.text = formattedAdress


                            binding.address.text = formattedAdress

                            myaddress = formattedAdress


                        } else {
                            val formatAddress2 =
                                "${address.premises} ${address.postalCode} ${address.subLocality} ${address.subAdminArea} ${address.locality}  ${address.adminArea} "

                            myaddress = formatAddress2


                            binding.locationName.text =
                                formatAddress2
                            binding.address.text =
                                formatAddress2

                        }


                        address.apply {
                            Log.d(
                                "TAGGGGGGGG", "getAddressFromLocation: $adminArea $subAdminArea" +
                                        " $locality $subLocality $countryCode $countryName $extras $featureName $locale $maxAddressLineIndex " +
                                        "${address.postalCode}  $subThoroughfare $thoroughfare $premises"
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
                binding.bigProfile.isClickable = false
                /*val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)*/
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Please Allow Camera permission", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(this, "Please Allow Camera permission", Toast.LENGTH_SHORT).show()
            }

        }

    }


    private fun setFinalDialog(

        latitude: String,
        longitude: String,
        address: String
    ) {
        binding.checkinCL.visibility = View.GONE

        binding.finalLayoutCL.visibility = View.VISIBLE
        binding.nameAtfinal.text = employeeName
        binding.designationAtfinal.text = employeeDesignation
        binding.shiftAtfinal.text = "${siteSelect} $shiftSelect $formattedDate"
        binding.latlgTxt.text = "$latitude $longitude"
        binding.address.text = myaddress


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
                                .toInt() == 1){

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
                                insertAttendance(
                                    "sams",
                                    imeiPhone,
                                    empNumber.toString(),
                                    emp?.AsmtID.toString(),
                                    empNumber.toString(),
                                    "IN",
                                    time.toString(),
                                    latitude = mLatitude.toString(),
                                    mLongitude.toString(),
                                    mAltitude.toString(),
                                    base64Image ?: "",
                                    emp?.LocationAutoID.toString(),
                                    emp?.ClientCode.toString(),
                                    siteSelect.toString(),
                                    myaddress.toString()
                                )
                            }

                        }else{
                            Toast.makeText(this@CheckInScreen, "attendance is already marked ", Toast.LENGTH_SHORT).show()
                        }
                        }
 else {
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
                if (response.isSuccessful) {
                    Log.d("TAGGGGGGGG", "onResponse: attendance inserted")
                    Toast.makeText(this@CheckInScreen, "attendance marked", Toast.LENGTH_SHORT)
                        .show()
                    binding.cameraPreviewView.visibility = View.GONE

                    binding.checkinCL.visibility = View.GONE
                    setFinalDialog(mLatitude.toString(), mLongitude.toString(), myaddress.toString())
                    val delayMillis = 5000L
                    Handler().postDelayed({
                        finish()
                        val intent = Intent(this@CheckInScreen, CheckOutScreen::class.java)
                        startActivity(intent)
                    }, delayMillis)

                } else {
                    Log.d("TAGGGGGGGG", "onResponse:error ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<AttendanceResponse?>, t: Throwable) {
                Log.d("TAGGGGGGGGGGG", "onFailure: attendance failed")
            }
        })

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getIMEI(context: Context): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // Check for the READ_PHONE_STATE permission
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Check for the Android version to handle changes in permissions starting from Android 10
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.READ_PHONE_STATE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("TAGGGGGGG", "getIMEI: this is imei no : ${telephonyManager.imei}")
                    telephonyManager.imei ?: ""
                } else {
                    ""
                }
            } else {
                telephonyManager.imei ?: ""
            }
        } else {
            return ""
        }
    }

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
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(
                        applicationContext,
                        "Error capturing image",
                        Toast.LENGTH_SHORT
                    ).show()
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

}

