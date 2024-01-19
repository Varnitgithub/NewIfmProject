package com.example.ifmapp.activities

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.RetrofitInstance
import com.example.ifmapp.databinding.ActivityMobileRegisterScreenBinding
import com.example.ifmapp.modelclasses.verifymobile.InputMobileRegister
import com.example.ifmapp.modelclasses.verifymobile.OtpSend
import com.example.ifmapp.modelclasses.verifymobile.OtpSendItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MobileRegisterScreen : AppCompatActivity() {
    private lateinit var binding: ActivityMobileRegisterScreenBinding
    private var mobileNumber: String? = null
    private var otp: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_mobile_register_screen)
        val retrofitInstance = RetrofitInstance.apiInstance
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile_register_screen)

        binding.wayToSignup.setOnClickListener {
            startActivity(Intent(this, SignUpScreen::class.java))
        }

        binding.btnContinue.setOnClickListener {
            if (binding.otp1.text.isEmpty() && binding.otp2.text.isEmpty() && binding.otp3.text.isEmpty() && binding.otp4.text.isEmpty()) {


                if (binding.mobileNoEdt.text.isNotEmpty()) {
                    if (binding.mobileNoEdt.text.toString().length == 10) {
                        mobileNumber = binding.mobileNoEdt.text.toString().trim()
                        retrofitInstance.registeredMobileNumber(
                            "sams",
                            mobileNumber = binding.mobileNoEdt.text.toString()
                        ).enqueue(object : Callback<OtpSend?> {
                            override fun onResponse(
                                call: Call<OtpSend?>,
                                response: Response<OtpSend?>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@MobileRegisterScreen, "otp send", Toast.LENGTH_SHORT).show()

                                    binding.otpSectionLL.visibility = View.VISIBLE
                                } else {
                                    Toast.makeText(this@MobileRegisterScreen, "otp not send", Toast.LENGTH_SHORT).show()

                                    binding.otpSectionLL.visibility = View.VISIBLE
                                }

                            }

                            override fun onFailure(call: Call<OtpSend?>, t: Throwable) {
                                Toast.makeText(this@MobileRegisterScreen, "otp failed", Toast.LENGTH_SHORT).show()

                                binding.otpSectionLL.visibility = View.VISIBLE


                            }
                        })
                    } else {
                        Toast.makeText(this, "Mobile no should be 10 digit", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_SHORT)
                        .show()

                }
            } else {

                if (binding.otp1.text.isNotEmpty() && binding.otp2.text.isNotEmpty()
                    && binding.otp3.text.isNotEmpty() && binding.otp4.text.isNotEmpty()
                ) {
                    val mobileOtp = "${binding.otp1.text.toString().trim()}${binding.otp2.text.toString().trim()}" +
                            "${binding.otp3.text.toString().trim()}${binding.otp4.text.toString().trim()}"
                    Log.d("TGGGGGGGG", "onCreate: $mobileOtp is the otp")
                    retrofitInstance.verifyMobileNumber("sams",mobileNumber?:"",mobileOtp).enqueue(object : Callback<Void?> {
                        override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                            if (response.isSuccessful){
                                Toast.makeText(this@MobileRegisterScreen, "otp verified", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this@MobileRegisterScreen, "otp not verified", Toast.LENGTH_SHORT).show()

                            }
                        }

                        override fun onFailure(call: Call<Void?>, t: Throwable) {
                            Toast.makeText(this@MobileRegisterScreen, "otp failed", Toast.LENGTH_SHORT).show()

                        }
                    })

                } else {
                    Toast.makeText(this, "please enter valid otp", Toast.LENGTH_SHORT).show()
                }

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

            override fun afterTextChanged(s: Editable?) {

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
                    if (editTexts[3].text.toString().isNotEmpty()) {
                        Toast.makeText(this@MobileRegisterScreen, "press continue", Toast.LENGTH_SHORT).show()
                    }
                    editTexts[i].inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                }


                override fun afterTextChanged(s: Editable?) {

                }
            })

            editTexts[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    // If backspace is pressed, move the focus to the previous EditText
                    if (i > 0) {
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

                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }
}