package com.example.ifmapp.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.apiinterface.ApiInterface
import com.example.ifmapp.databinding.ActivityMobileRegisterScreenBinding
import com.example.ifmapp.modelclasses.verifymobile.OtpSend
import com.example.ifmapp.modelclasses.verifymobile.VerifyOtpResponse
import com.example.ifmapp.toast.CustomToast
import com.example.ifmapp.utils.IMEIGetter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MobileRegisterScreen : AppCompatActivity() {
    private lateinit var binding: ActivityMobileRegisterScreenBinding
    private var mobileNumber: String? = null
    private var otp: String? = null
    private lateinit var retrofitInstance: ApiInterface
    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private val READ_PHONE_PERMISSION = 123
    private var oneTimeResend = true
    private val BACKGROUND_LOCATION_CODE = 111
    private var countDownTimer: CountDownTimer? = null
    private val initialTimeMillis: Long = 60000 // 60 seconds
    private val countDownIntervalMillis: Long = 1000 // 1 second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_mobile_register_screen)
        retrofitInstance = RetrofitInstance.apiInstance
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile_register_screen)
        binding.resentOtp.visibility = View.GONE
        binding.otpSectionLL.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        /*   if (checkPermission()) {

           } else {
               requestPermission()
           }

   */
        //getPhoneRead()


//
        binding.btnContinue.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            sendOTP()
        }

        binding.resentOtp.setOnClickListener {
            if (oneTimeResend) {
                startCountdownTimer()
                sendOTP()
                binding.resentOtp.isEnabled = false
                binding.resentOtp.visibility = View.GONE
                oneTimeResend = false
            }


        }
        binding.mobileNoEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentLength = s?.length ?: 0
                if (currentLength == 10) {
                    binding.btnContinue.setTextColor(resources.getColor(R.color.white))
                    binding.btnContinue.setBackgroundResource(R.drawable.button_back)

                } else {
                    binding.btnContinue.setTextColor(resources.getColor(R.color.check_btn))
                    binding.btnContinue.setBackgroundResource(R.drawable.button_backwhite)
                }
            }

            override fun afterTextChanged(s: Editable?) {/*
                val stringWithoutSpaces = s.toString().replace(" ", "")
                if (s.toString() != stringWithoutSpaces) {
                    binding.mobileNoEdt.setText(stringWithoutSpaces)
                    binding.mobileNoEdt.setSelection(stringWithoutSpaces.length) // Move cursor to the end
                }*/
            }
        })

        val editTexts = listOf(binding.otp1, binding.otp2, binding.otp3, binding.otp4)
        for (i in 0 until editTexts.size) {

            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    editTexts[i].inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                    if (editTexts[3].text.toString().isNotEmpty()) {
                        editTexts[i].inputType =
                            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                        binding.btnContinue.isEnabled = true
                        binding.btnContinue.isClickable = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            editTexts[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (i > 0) {
                        editTexts[i].inputType =
                            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        editTexts[i].text.clear()
                        editTexts[i - 1].requestFocus()
                    }
                    true
                } else {
                    false
                }
            }
        }
        setupOtpEditTextListeners()

    }

    private fun setupOtpEditTextListeners() {
        binding.otp1.addTextChangedListener(createOtpTextWatcher(binding.otp2))
        binding.otp2.addTextChangedListener(createOtpTextWatcher(binding.otp3))
        binding.otp3.addTextChangedListener(createOtpTextWatcher(binding.otp4))
        binding.otp4.addTextChangedListener(createOtpTextWatcher(null))
    }

    private fun createOtpTextWatcher(nextEditText: EditText?): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.length == 1) {
                    nextEditText?.requestFocus()
                    nextEditText?.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_TEXT_VARIATION_PASSWORD

                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun startCountdownTimer() {
        countDownTimer = object : CountDownTimer(initialTimeMillis, countDownIntervalMillis) {

            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                if (secondsRemaining.toInt().toString().length == 2) {
                    binding.time.text = "00:$secondsRemaining"
                } else {
                    binding.time.text = "00:0$secondsRemaining"

                }
                // binding.resentOtp.visibility = View.VISIBLE

            }

            override fun onFinish() {
                // Countdown timer finished, handle the event
                if (oneTimeResend){
                binding.resentOtp.isEnabled = true
                    binding.resentOtp.visibility = View.VISIBLE
                }



            }
        }


        countDownTimer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the countdown timer to avoid leaks
        countDownTimer?.cancel()
    }

    private fun sendOTP() {
        binding.progressBar.visibility = View.VISIBLE
        if (binding.mobileNoEdt.text.isNotEmpty()) {
            if (binding.mobileNoEdt.text.toString().length == 10) {
                binding.btnContinue.isClickable = false
                binding.mobileNoEdt.isClickable = false

                mobileNumber = binding.mobileNoEdt.text.toString().trim()
                retrofitInstance.validateMobileNumber(
                    "sams",
                    mobileNumber = binding.mobileNoEdt.text.toString()
                ).enqueue(object : Callback<VerifyOtpResponse?> {
                    override fun onResponse(
                        call: Call<VerifyOtpResponse?>,
                        response: Response<VerifyOtpResponse?>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()?.get(0)?.MessageID?.toInt() == 1) {


                                Log.d("TAGGGGGGGG", "onResponse: user is valid")

                                if (binding.otp1.text.isEmpty() && binding.otp2.text.isEmpty()
                                    && binding.otp3.text.isEmpty() && binding.otp4.text.isEmpty()
                                ) {


                                    if (binding.mobileNoEdt.text.isNotEmpty()) {
                                        if (binding.mobileNoEdt.text.toString().length == 10) {
                                            mobileNumber =
                                                binding.mobileNoEdt.text.toString().trim()
                                            retrofitInstance.registeredMobileNumber(
                                                "sams",
                                                mobileNumber = binding.mobileNoEdt.text.toString()
                                            ).enqueue(object : Callback<OtpSend?> {
                                                override fun onResponse(
                                                    call: Call<OtpSend?>,
                                                    response: Response<OtpSend?>
                                                ) {
                                                    if (response.isSuccessful) {
                                                        binding.progressBar.visibility = View.GONE

                                                        Log.d(
                                                            "TAGGGGGGGG",
                                                            "onResponse: otp send successfully"
                                                        )
                                                        binding.mobileNoEdt.isEnabled = false
                                                        binding.btnContinue.isEnabled = false
                                                        binding.btnContinue.isClickable = false
                                                        binding.resentOtp.visibility = View.GONE
                                                        binding.otpSectionLL.visibility =
                                                            View.VISIBLE
                                                        startCountdownTimer()

                                                    } else {

                                                        Log.d(
                                                            "TAGGGGGGGG",
                                                            "onResponse: otp send failure"
                                                        )
                                                        binding.progressBar.visibility = View.GONE


                                                    }

                                                }

                                                override fun onFailure(
                                                    call: Call<OtpSend?>,
                                                    t: Throwable
                                                ) {
                                                    Log.d("TAGGGGGGGG", "onFailure: ${t.message}")
                                                    binding.progressBar.visibility = View.GONE


                                                    // binding.otpSectionLL.visibility = View.VISIBLE


                                                }
                                            })
                                        } else {
                                            binding.progressBar.visibility = View.GONE

                                            CustomToast.showToast(
                                                this@MobileRegisterScreen,
                                                "Mobile no should be 10 digit"
                                            )
                                        }
                                    } else {
                                        binding.progressBar.visibility = View.GONE

                                        CustomToast.showToast(
                                            this@MobileRegisterScreen,
                                            "Please enter your mobile number"
                                        )

                                    }
                                } else {

                                    if (binding.otp1.text.isNotEmpty() && binding.otp2.text.isNotEmpty()
                                        && binding.otp3.text.isNotEmpty() && binding.otp4.text.isNotEmpty()
                                    ) {
                                        val mobileOtp = "${binding.otp1.text.toString().trim()}${
                                            binding.otp2.text.toString().trim()
                                        }" +
                                                "${binding.otp3.text.toString().trim()}${
                                                    binding.otp4.text.toString().trim()
                                                }"
                                        retrofitInstance.verifyMobileNumber(
                                            "sams",
                                            mobileNumber ?: "",
                                            mobileOtp
                                        )
                                            .enqueue(object : Callback<VerifyOtpResponse?> {
                                                override fun onResponse(
                                                    call: Call<VerifyOtpResponse?>,
                                                    response: Response<VerifyOtpResponse?>
                                                ) {
                                                    if (response.isSuccessful) {
                                                        if (response.body()
                                                                ?.get(0)?.MessageID?.toInt() == 1
                                                        ) {
                                                            Log.d(
                                                                "TAGGGGGGGGGG",
                                                                "onResponse: verify otp"
                                                            )
                                                            val intent = Intent(
                                                                this@MobileRegisterScreen,
                                                                GnereratePinCodeScreen::class.java
                                                            )
                                                            intent.putExtra(
                                                                "mobileNumber",
                                                                mobileNumber
                                                            )
                                                            startActivity(intent)
                                                            binding.progressBar.visibility =
                                                                View.GONE

                                                        } else {
                                                            CustomToast.showToast(
                                                                this@MobileRegisterScreen,
                                                                "${
                                                                    response.body()
                                                                        ?.get(0)?.MessageString
                                                                }"
                                                            )

                                                            binding.progressBar.visibility =
                                                                View.GONE
                                                            binding.otp1.text.clear()
                                                            binding.otp2.text.clear()
                                                            binding.otp3.text.clear()
                                                            binding.otp4.text.clear()
                                                        }


                                                    } else {

                                                        Log.d(
                                                            "TAGGGGGGGGGG",
                                                            "onResponse: otp verification failed"
                                                        )
                                                        binding.progressBar.visibility = View.GONE
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<VerifyOtpResponse?>,
                                                    t: Throwable
                                                ) {

                                                    Log.d("TAGGGGGGGGGG", "onFailure: ${t.message}")
                                                    binding.progressBar.visibility = View.GONE

                                                }
                                            })

                                    } else {

                                    }

                                }
                            } else {
                                Toast.makeText(
                                    this@MobileRegisterScreen,
                                    "${response.body()?.get(0)?.MessageString}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.progressBar.visibility = View.GONE
                            }


                        } else {
                            CustomToast.showToast(this@MobileRegisterScreen, "user is not valid")
                            binding.progressBar.visibility = View.GONE
                        }
                    }

                    override fun onFailure(call: Call<VerifyOtpResponse?>, t: Throwable) {
                        Log.d("TAGGGGGGGGGGG", "onFailure: user validation failed")
                        binding.progressBar.visibility = View.GONE
                    }
                })


            } else {
                CustomToast.showToast(this@MobileRegisterScreen, "Mobile no should be 10 digit")

            }
        } else {
            CustomToast.showToast(this@MobileRegisterScreen, "Please enter your mobile number")

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

    private fun requestPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )

    }

    private fun checkMockLocation(): Boolean {
        // Check if mock locations are enabled
        return Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ALLOW_MOCK_LOCATION
        ) != "0"
    }

    //    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            IMEIGetter.PERMISSION_REQUEST_READ_PHONE_STATE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission granted, you can now proceed with the logic
//                    // For example, you might call getIMEI() here
//                    val imeiGetter = IMEIGetter(this)
//                    val imei = imeiGetter.getIMEI()
//                    // Do something with the IMEI
//                } else {
//                    // Permission denied, handle accordingly
//                    // For example, inform the user or take appropriate action
//                    // You can also check if shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
//                    // is true to show a rationale to the user
//                    Toast.makeText(
//                        this,
//                        "Permission denied for READ_PHONE_STATE",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//            // Handle other permission requests if needed
//        }
//    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isMockLocation()) {
                    Log.d("TAGGGGGGGGGG", "onRequestPermissionsResult: getting location")
                } else {
                    CustomToast.showToast(
                        this@MobileRegisterScreen,
                        "mock location is enabled please disable it"
                    )
                }
            }
        }
    }

    private fun isMockLocation(): Boolean {
        try {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            return location?.isFromMockProvider ?: false
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        return false
    }


    private fun getPhoneRead() {
        // Check if the permission is already granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val imeiGetter = IMEIGetter(this)
            val imei = imeiGetter.getIMEI()
            // Do something with the IMEI
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                READ_PHONE_PERMISSION
            )
        }
    }

    override fun onPause() {
        super.onPause()
    }


}