package com.example.ifmapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.ifmapp.MainActivity

import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.modelclasses.postmodel.PostModel
import com.example.ifmapp.modelclasses.verifymobile.VerifyOtpResponse
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.GlobalLocation
import com.example.ifmapp.utils.ShiftDetailsObject
import com.example.ifmapp.utils.UserObject
import com.example.ifmapp.utils.UtilModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScannerScreen : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE = 11

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var LOCATION_PERMISSION_REQUEST_CODE = 111


    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null

    private lateinit var codeScanner: CodeScanner
    private var otp: String? = null
    private var siteSelect: String? = null
    private var shiftSelect: String? = null
    private var iNOUTStatus: String? = null
    private var userName: String? = null
    private var shiftTimingList: String? = null
    private lateinit var retrofitInstance: ApiInterface


    override fun onResume() {
        super.onResume()
        if (checkCameraPermission()) {
            startScanning()
            codeScanner.startPreview()
        }
        else{
            requestCameraPermission()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_screen)
        retrofitInstance = RetrofitInstance.apiInstance
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setIntervalMillis(500)
                .build()

        val scannerView: CodeScannerView = findViewById(R.id.scannerView)
        codeScanner = CodeScanner(this, scannerView)



        iNOUTStatus = intent.getStringExtra("INOUTStatus")
        otp = intent.getStringExtra("mPIN")
        userName = intent.getStringExtra("empName")
        shiftSelect = intent.getStringExtra("shiftSelect")
        siteSelect = intent.getStringExtra("siteSelect")
        shiftTimingList = intent.getStringExtra("shiftTimingList")

        createLocationRequest()

        Log.d(
            "TAGGGGG",
            "onCreate ofscanner:site ${ShiftDetailsObject.siteSElect} post ${ShiftDetailsObject.asmtId}"
        )

        getLastLocation()
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
    fun getLastLocation() {
        fusedLocationProviderClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                // Got last known location
                location?.let {
                    GlobalLocation.location = UtilModel(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        location.altitude.toString())
                    Log.d(
                        "TAGGGGAAAA",
                        "getLastLocation: this is global loc 2. ${GlobalLocation.location}")
                }
            }
    }
    private fun startScanning() {
      //  val scannerView: CodeScannerView = findViewById(R.id.scannerView)
       // codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback { result ->
            runOnUiThread {
                if (result.toString().isNotEmpty()) {
                }
                checkPostWithSite(result.toString(), result.toString())
               // startActivity(intent)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //startScanning()
            } else {
                CustomToast.showToast(this@ScannerScreen, "permision declined")
                requestCameraPermission()
            }
        }
    }

    private fun checkPostWithSite(postId: String, result: String) {

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
                        for (i in 0 until response.body()!!.size) {
                            if (response.body()?.get(i)?.MessageID?.toInt() == 1) {
                                ShiftDetailsObject.post = result
                                codeScanner.stopPreview()
                                codeScanner.releaseResources()
                                val intent = Intent(this@ScannerScreen, CheckInScreen::class.java)
                                intent.putExtra("shiftTimingList", shiftTimingList)
                                startActivity(intent)
                                finish()

                            } else {
                                for (i in 0 until response.body()!!.size) {
                                    if (response.body()?.get(i)?.MessageID?.toInt() == 1) {
                                        CustomToast.showToast(
                                            this@ScannerScreen,
                                            response.body()?.get(i)?.MessageString.toString()
                                        )
                                    } else {

                                    }
                                }
                                codeScanner.stopPreview()
                                startActivity(Intent(this@ScannerScreen, MainActivity::class.java))
                                finish()
                            }
                        }
                    } else {
                        CustomToast.showToast(this@ScannerScreen, "Response Unsuccessful")
                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponse?>, t: Throwable) {

                }
            })


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
            CAMERA_REQUEST_CODE
        )
    }


    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
        Log.d("TAGGGGGGGGGGGGGGGGGGGGGGGGGGG", "onPause: camera resources released")
//  closeCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        codeScanner.releaseResources()
        removeLocationUpdates()
        // closeCamera()
    }

    private fun closeCamera() {
       /* cameraCaptureSession?.close()
        cameraDevice?.close()

        cameraCaptureSession = null
        cameraDevice = null*/
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ScannerScreen, MainActivity::class.java))
        super.onBackPressed()

    }
}