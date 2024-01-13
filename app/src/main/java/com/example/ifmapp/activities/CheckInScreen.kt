package com.example.ifmapp.activities

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.ifmapp.Locationmodel
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityCheckInScreenBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import java.io.IOException
import java.util.Locale

class CheckInScreen : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var realtimeLocationLiveData: MutableLiveData<Locationmodel>
    private lateinit var binding: ActivityCheckInScreenBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private var isLocationFetched = false
    private var address: String? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private var userBitmap: Bitmap? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 200
    private val CAMERA_PERMISSION_CODE = 101


    private var isPermissionDenied = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_in_screen)

        //initialization
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        realtimeLocationLiveData = MutableLiveData()

        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


        realtimeLocationLiveData.observe(this){
            Log.d("TAGGGGGGGGG", "onCreate: loc ${it.longitude} ${it.latitude}")
            Toast.makeText(this, " loc ${it.longitude} ${it.latitude}", Toast.LENGTH_SHORT).show()
        }

        binding.finalLayoutCL.visibility = View.GONE
        binding.checkinCL.visibility = View.VISIBLE

        binding.btnSubmit.setOnClickListener {

            binding.btnSubmit.setTextColor(resources.getColor(R.color.check_btn))
            binding.btnSubmit.setBackgroundResource(R.drawable.button_backwhite)
            binding.btnRetake.setTextColor(resources.getColor(R.color.white))
            binding.btnRetake.setBackgroundResource(R.drawable.button_back)



            setFinalDialog(
                userBitmap!!,
                latitude.toString(),
                longitude.toString(),
                address.toString()
            )

            val animator = ObjectAnimator.ofFloat(binding.blinkingBtn, "alpha", 1f, 0f)
            animator.duration = 500 // Set the duration for one iteration (in milliseconds)
            animator.repeatMode = ObjectAnimator.REVERSE // Reverse the animation
            animator.repeatCount = ObjectAnimator.INFINITE // Repeat indefinitely

            // Start the animation
            animator.start()
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
            if (checkPermission()/*&&isGpsEnabled&&isNetworkEnabled*/) {
                Log.d("TAGGGGGGGG", "onCreate:i am here ")
                //get location
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

                if (checkCameraPermission()) {
                    dispatchTakePictureIntent()
                } else {
                    requestCameraPermission()
                }
            } else {
                Log.d("TAGGGGGGGG", "onCreate:i am here 2")

                requestLocation()
            }

        }

        binding.btnCross.setOnClickListener {
            startActivity(Intent(this, DashBoardScreen::class.java))
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocation() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

                if (checkCameraPermission()) {
                    dispatchTakePictureIntent()

                } else {
                    requestCameraPermission()
                }
            }
        } else if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                dispatchTakePictureIntent()
            }
        }
    }

    private fun getAddressFromLocation(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                binding.address.text =
                    "${address.locality} ${address.subLocality} ${address.adminArea} ${address.subAdminArea}"

            }
        } catch (e: IOException) {
            Log.e("Geocoding", "Error getting address from location: $e")
        }

        return "Address not found"
    }

    override fun onLocationChanged(location: Location) {
        realtimeLocationLiveData.postValue(Locationmodel(location.latitude, location.longitude))
        latitude = location.latitude.toString()
        longitude = location.longitude.toString()
        address = getAddressFromLocation(this, location.latitude, location.longitude)
        isLocationFetched = true
        Log.d("TAGGGGG", "onLocationChanged:thisi=s ${location.latitude}")
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
            binding.checkInlatlong.text = "$latitude  $longitude"
            var address = getAddressFromLocation(
                this@CheckInScreen,
                latitude?.toDouble() ?: 0.0,
                longitude?.toDouble() ?: 0.0
            )
            binding.checkInAdress.text = "$latitude  $longitude"

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


    fun setFinalDialog(image: Bitmap, latitude: String, longitude: String, address: String) {
        binding.checkinCL.visibility = View.GONE
        binding.finalLayoutCL.visibility = View.VISIBLE
        binding.profileFinal.setImageBitmap(image)
        binding.latlgTxt.text = "$latitude $longitude"
        binding.address.text = address
    }

    @SuppressLint("MissingPermission")
    private fun picture() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

}