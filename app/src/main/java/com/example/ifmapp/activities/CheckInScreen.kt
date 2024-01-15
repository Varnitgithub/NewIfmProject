package com.example.ifmapp.activities


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.provider.Settings
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.ifmapp.Locationmodel
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityCheckInScreenBinding
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
import java.io.IOException
import java.util.Locale

class CheckInScreen : AppCompatActivity() {
    private lateinit var realtimeLocationLiveData: MutableLiveData<Locationmodel>
    private lateinit var binding: ActivityCheckInScreenBinding
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var address: String? = null
    private var mLatitude: String? = null
    private var mLongitude: String? = null
    private var userBitmap: Bitmap? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val CAMERA_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in_screen)

        //initialization
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

                    // Use suspend functions or other asynchronous mechanisms if needed
                    getAddressFromLocation(
                        this@CheckInScreen,
                        mLatitude?.toDouble() ?: 0.0,
                        mLongitude?.toDouble() ?: 0.0
                    )

                    realtimeLocationLiveData.postValue(
                        Locationmodel(
                            location?.latitude,
                            location?.longitude
                        )
                    )

                    // Other code to be executed on the main thread after fetching location
                    // ...
                }
            }
        }
        realtimeLocationLiveData = MutableLiveData()


        createLocationRequest()

        realtimeLocationLiveData.observe(this) {
            binding.checkInlatlong.text = "$mLatitude $mLongitude"
            it.let {
                it.latitude?.let { it1 ->
                    it.longitude?.let { it2 ->
                        getAddressFromLocation(
                            this, it1,
                            it2
                        )
                    }
                }

            }
        }

        binding.finalLayoutCL.visibility = View.GONE
        binding.checkinCL.visibility = View.VISIBLE

        binding.btnSubmit.setOnClickListener {

            binding.btnSubmit.setTextColor(resources.getColor(R.color.check_btn))
            binding.btnSubmit.setBackgroundResource(R.drawable.button_backwhite)
            binding.btnRetake.setTextColor(resources.getColor(R.color.white))
            binding.btnRetake.setBackgroundResource(R.drawable.button_back)

            if (userBitmap != null && mLatitude != null && mLongitude != null) {
                setFinalDialog(
                    userBitmap!!,
                    mLatitude.toString(),
                    mLongitude.toString(),
                    address.toString()
                )
                startBlinkAnimation()

                val delayMillis = 8000L

                Handler().postDelayed({
                    finish()
                    // Start another activity
                    val intent = Intent(this@CheckInScreen, CheckOutScreen::class.java)
                    startActivity(intent)

                }, delayMillis)

            }else{
                binding.btnSubmit.isEnabled = false
            }
        }

        binding.btnRetake.setOnClickListener {
            if (userBitmap != null) {
                binding.btnRetake.setTextColor(resources.getColor(R.color.check_btn))
                binding.btnRetake.setBackgroundResource(R.drawable.button_backwhite)
                binding.btnSubmit.setTextColor(resources.getColor(R.color.white))
                binding.btnSubmit.setBackgroundResource(R.drawable.button_back)
                dispatchTakePictureIntent()
            }



        }

        binding.bigProfile.setOnClickListener {

            if (checkCameraPermission()) {
                dispatchTakePictureIntent()
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
                        binding.address.text =
                            "${address.locality} ${address.subLocality} ${address.adminArea} ${address.subAdminArea}"
                    }
                }
            } catch (e: IOException) {
                Log.e("Geocoding", "Error getting address from location: $e")
            }
        }
    }



    private fun dispatchTakePictureIntent() {
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
            binding.bigProfile.setImageBitmap(imageBitmap)
            userBitmap = imageBitmap


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

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
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
        binding.latlgTxt.text = "$latitude $longitude"
        binding.address.text = address
    }

    @SuppressLint("MissingPermission")
    fun createLocationRequest() {
        try {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest!!,
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
}