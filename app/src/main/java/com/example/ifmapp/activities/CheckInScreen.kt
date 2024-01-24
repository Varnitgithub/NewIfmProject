package com.example.ifmapp.activities


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databasedb.EmployeeDB
import com.example.ifmapp.databasedb.EmployeePinDao
import com.example.ifmapp.databinding.ActivityCheckInScreenBinding
import com.example.ifmapp.modelclasses.attendance_response.AttendanceResponse
import com.example.ifmapp.modelclasses.geomappedsite_model.GeoMappedResponse
import com.example.ifmapp.utils.IMEIGetter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CheckInScreen : AppCompatActivity() {

    private var employeeId: String? = null
    private lateinit var binding: ActivityCheckInScreenBinding
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var address: String? = null
    private var myaddress: String? = null
    private var mLatitude: String? = null
    private var mLongitude: String? = null
    private var mAltitude: String? = null
    private var userBitmap: Bitmap? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val CAMERA_PERMISSION_CODE = 101
    private lateinit var imeiGetter: IMEIGetter
    private var imageInString: String? = null
    private var employeeName: String? = null
    private var employeeDesignation: String? = null
    private var otp: String? = null
    private var siteSelect: String? = null
    private var shiftSelect: String? = null
    private var time: String? = null
    private var locationAutoID: String? = null
    private var empNumber: String? = null
    private lateinit var mCamera: Camera
    private lateinit var retrofitInstance: ApiInterface
    private lateinit var formattedDate: String
    private var currenTtime: String? = null
    private lateinit var currentDate: Date
    private lateinit var employeePinDao: EmployeePinDao

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in_screen)
        retrofitInstance = RetrofitInstance.apiInstance
        imeiGetter = IMEIGetter(this)
        binding.checkinCL.visibility = View.VISIBLE
        binding.finalLayoutCL.visibility = View.GONE
        currentDate = Date()
        formattedDate = getFormattedDate(currentDate)

        employeePinDao = EmployeeDB.getInstance(this).employeePinDao()
        otp = intent.getStringExtra("mPIN")
        siteSelect = intent.getStringExtra("siteSelect")
        shiftSelect = intent.getStringExtra("shiftSelect")
        empNumber = intent.getStringExtra("empNumber")

        Log.d("TAGGGGGGG", "onCreate: this is $empNumber")


        time = getCurrentTime()

        if (otp.toString().isNotEmpty()) {

            otp?.let { getEmployee(otp = it) }

        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setIntervalMillis(500)
                .build()



        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                // Use a coroutine to perform location-related tasks asynchronously
                lifecycleScope.launch(Dispatchers.IO) {
                    val location = locationResult.lastLocation
                    mLatitude = location?.latitude.toString()
                    mLongitude = location?.longitude.toString()
                    mAltitude = location?.altitude.toString()
                    binding.checkInlatlong.text = "${location?.latitude} ${location?.longitude}"

                    // Use suspend functions or other asynchronous mechanisms if needed
                    getAddressFromLocation(
                        this@CheckInScreen,
                        mLatitude?.toDouble() ?: 0.0,
                        mLongitude?.toDouble() ?: 0.0
                    )

                }
            }
        }

        createLocationRequest()


        binding.finalLayoutCL.visibility = View.GONE
        binding.checkinCL.visibility = View.VISIBLE

        binding.btnSubmit.setOnClickListener {

            binding.btnSubmit.setTextColor(resources.getColor(R.color.white))
            binding.btnSubmit.setBackgroundResource(R.drawable.button_back)
            binding.btnRetake.setTextColor(resources.getColor(R.color.check_btn))
            binding.btnRetake.setBackgroundResource(R.drawable.button_backwhite)

            if (imageInString != null && mLatitude != null && mLongitude != null) {

                getGeoMappedSites("sams", locationAutoID!!, mLatitude!!, mLongitude!!)



                startBlinkAnimation()

                val delayMillis = 6000L
                Handler().postDelayed({
                    finish()
                    Log.d("TAGGGGGG", "onCreate:sssssss $shiftSelect is shift $time is time")

                    // Start another activity
                    val intent = Intent(this@CheckInScreen, CheckOutScreen::class.java)
                    intent.putExtra("shiftTime", shiftSelect)
                    intent.putExtra("currentTime", time)
                    intent.putExtra("employeeName", employeeName)
                    intent.putExtra("employeeDesignation", employeeDesignation)
                    intent.putExtra("currentTime", time)
                    intent.putExtra("address", myaddress)
                    intent.putExtra("siteSelect", siteSelect)
                    intent.putExtra("shiftSelect", shiftSelect)
                    startActivity(intent)

                }, delayMillis)

            }

        }

        Log.d("TAGGGGGG", "onCreate: $shiftSelect is shift $time is time")

        binding.btnRetake.setOnClickListener {
            if (imageInString != null) {

                binding.btnRetake.setTextColor(resources.getColor(R.color.white))
                binding.btnRetake.setBackgroundResource(R.drawable.button_back)
                binding.btnSubmit.setTextColor(resources.getColor(R.color.check_btn))
                binding.btnSubmit.setBackgroundResource(R.drawable.button_backwhite)
                binding.btnRetake.isEnabled = true
                openFrontFacingCamera()

                // dispatchTakePictureIntent()
            }
        }
        var imeei = getIMEI(this)

        Log.d("TAGGGGGGGGG", "onCreate: thid id mmei $imeei")
        binding.bigProfile.setOnClickListener {

            if (checkCameraPermission()) {
                openFrontFacingCamera()
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
                    var address: Address = addresses[0]
                    withContext(Dispatchers.Main) {
                        myaddress =
                            "${address.locality} ${address.subLocality} ${address.adminArea} ${address.subAdminArea}"
                        binding.locationName.text =
                            "${address.locality} ${address.subLocality} ${address.adminArea} ${address.subAdminArea}"
                        binding.address.text =
                            "${address.locality} ${address.subLocality} ${address.adminArea} ${address.subAdminArea}"
                    }
                }
            } catch (e: IOException) {
                Log.e("Geocoding", "Error getting address from location: $e")
            }
        }
    }


    /*  private fun dispatchTakePictureIntent() {
          val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

          if (takePictureIntent.resolveActivity(packageManager) != null) {
              // Specify the camera facing
              takePictureIntent.putExtra(
                  "android.intent.extras.CAMERA_FACING",
                  2
              ) // 1 corresponds to CameraInfo.CAMERA_FACING_FRONT

              startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
          }
      }

      override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
          super.onActivityResult(requestCode, resultCode, data)

          if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
              val imageBitmap = data?.extras?.get("data") as Bitmap
              // Do something with the captured image (e.g., display it in an ImageView)
              resizeAndSetBitmap(binding.bigProfile, imageBitmap, 313, 313)
               imageInString = bitmapToString(imageBitmap)
              userBitmap = imageBitmap
              Log.d("TAGGGGGGG", "onActivityResult: this is bitmap $imageBitmap")
              Log.d("TAGGGGGGG", "onActivityResult: this is string image $imageInString")


          }
      }*/

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

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFrontFacingCamera()//   dispatchTakePictureIntent()
            }
        }
    }


    private fun setFinalDialog(
        image: Bitmap,
        latitude: String,
        longitude: String,
        address: String
    ) {
        binding.checkinCL.visibility = View.GONE
        binding.finalLayoutCL.visibility = View.VISIBLE
        binding.profileFinal.setImageBitmap(image)
        binding.nameAtfinal.text = employeeName
        binding.designationAtfinal.text = employeeDesignation
        binding.shiftAtfinal.text = "${siteSelect} $shiftSelect $formattedDate"
        binding.latlgTxt.text = "$latitude $longitude"
        binding.address.text = address


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


    private fun startBlinkAnimation() {
        val fadeIn = AlphaAnimation(0.0f, 1.0f)
        fadeIn.duration = 300 // You can adjust the duration as needed
        fadeIn.interpolator = DecelerateInterpolator()

        val fadeOut = AlphaAnimation(1.0f, 0.0f)
        fadeOut.startOffset = 300 // Time to wait before starting fade out
        fadeOut.duration = 300 // You can adjust the duration as needed
        fadeOut.interpolator = DecelerateInterpolator()

        val animationSet = AnimationSet(true)
        animationSet.addAnimation(fadeIn)
        animationSet.addAnimation(fadeOut)

        // Set the AnimationListener to restart the animation when it ends
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                startBlinkAnimation()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        // Apply the animation to your layout
        binding.blinkingBtn.startAnimation(animationSet)
        binding.attendanceMarkedTxt.startAnimation(animationSet)
    }

    private fun checkMockLocation(): Boolean {
        // Check if mock locations are enabled
        return Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ALLOW_MOCK_LOCATION
        ) != "0"
    }

    private fun resizeAndSetBitmap(
        imageView: ImageView,
        bitmap: Bitmap?,
        targetWidth: Int,
        targetHeight: Int
    ) {
        if (bitmap == null) return

        val scaleFactor =
            calculateScaleFactor(bitmap.width, bitmap.height, targetWidth, targetHeight)

        val newWidth = (bitmap.width * scaleFactor).toInt()
        val newHeight = (bitmap.height * scaleFactor).toInt()

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

        imageView.setImageBitmap(resizedBitmap)
    }

    private fun calculateScaleFactor(
        originalWidth: Int,
        originalHeight: Int,
        targetWidth: Int,
        targetHeight: Int
    ): Float {
        val widthScale = targetWidth.toFloat() / originalWidth
        val heightScale = targetHeight.toFloat() / originalHeight

        return if (widthScale > heightScale) {
            heightScale
        } else {
            widthScale
        }
    }

    private fun getEmployee(otp: String) {

        employeePinDao.getcurrentEmployeeDetails(otp)
            .observe(this) {
                if (it != null) {
                    Log.d("TAGGGGGG", "onTextChanged:it is not null")

                    binding.userName.text = it.EmpName
                    binding.designation.text = it.Designation
                    binding.shifts.text = shiftSelect
                    employeeName = it.EmpName
                    //binding.locationName.text = address
                    employeeDesignation = it.Designation
                    locationAutoID = it.LocationAutoID


                } else {
                    Log.d("TAGGGGGGGGGGGG", "getEmployee: ")
                }
            }
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
                    if (response.isSuccessful) {

                        Log.d("TAGGGGGGGGGG", "onResponse: response Successfulllll")

                        val emp = response.body()?.get(0)
                        Log.d("TAGGGGGGG", "onResponse: this is $mAltitude")
                        if (myaddress != null) {

                            var imeiPhone = imeiGetter.getIMEI()
                            Log.d(
                                "TAGGGGG",
                                "onResponse:1${time} \n2 ${imeiPhone}\n" +
                                        "3${empNumber} 4${emp?.AsmtID} 5${mLatitude.toString()} 6 ${mLongitude.toString()}" +
                                        "7${mAltitude.toString()} 8${imageInString} 9${emp?.LocationAutoID} 10${emp?.ClientCode}" +
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
                                imageInString!!,
                                emp?.LocationAutoID.toString(),
                                emp?.ClientCode.toString(),
                                siteSelect.toString(),
                                myaddress.toString()
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

    /* setFinalDialog(
     userBitmap!!,
     mLatitude.toString(),
     mLongitude.toString(),
     address.toString()
 )*/


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
                    setFinalDialog(userBitmap!!, mLatitude!!, mLongitude!!, myaddress!!)
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

    private fun openFrontFacingCamera() {
        for (camNo in 0 until Camera.getNumberOfCameras()) {
            val camInfo = Camera.CameraInfo()
            Camera.getCameraInfo(camNo, camInfo)

            if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCamera = Camera.open(camNo)
            }
        }


        // no front-facing camera, use the first back-facing camera instead.
        // you may instead wish to inform the user of an error here...
        if (!::mCamera.isInitialized) {
            // no front-facing camera, use the first back-facing camera instead.
            // you may instead wish to inform the user of an error here...
            mCamera = Camera.open()
        }


        // Assuming the following code is inside a click listener for capturing the picture
        mCamera?.takePicture(null, null, pictureCallback)
    }

    private val pictureCallback = Camera.PictureCallback { data, _ ->
        // Decode the byte array to a Bitmap
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

        // Convert the Bitmap to a Base64-encoded string
        val encodedImage = bitmapToBase64(bitmap)

        // Log or use the encodedImage as needed
        Log.d("CameraActivity", "Base64 Image: $encodedImage")

        // Pass the encodedImage back to the calling activity (if needed)
        val resultIntent = Intent()
        resultIntent.putExtra("encodedImage", encodedImage)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val imageBytes = outputStream.toByteArray()

        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the camera in onDestroy to avoid memory leaks
        mCamera?.release()
        removeLocationUpdates()
    }
}