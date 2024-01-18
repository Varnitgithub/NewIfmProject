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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MobileRegisterScreen : AppCompatActivity() {
    private lateinit var binding: ActivityMobileRegisterScreenBinding

    private var otp:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_mobile_register_screen)
        var retrofitInstance = RetrofitInstance.apiInstance
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile_register_screen)

        binding.wayToSignup.setOnClickListener {
            startActivity(Intent(this, SignUpScreen::class.java))
        }

        binding.btnContinue.setOnClickListener {
            if (binding.mobileNoEdt.text.isNotEmpty()) {
                if (binding.mobileNoEdt.text.toString().length==10) {
                    Log.d("TAGGGGGG", "onCreate: ${binding.mobileNoEdt.text}")

                    retrofitInstance.registeredMobileNumber(
                        InputMobileRegister(
                            "sams",
                            binding.mobileNoEdt.text.toString().trim()
                        )
                    ).enqueue(object : Callback<OtpSend?> {
                        override fun onResponse(call: Call<OtpSend?>, response: Response<OtpSend?>) {
                            Toast.makeText(
                                this@MobileRegisterScreen,
                                "Otp Send ${response.body()}",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.otpSectionLL.visibility = View.VISIBLE
                        }

                        override fun onFailure(call: Call<OtpSend?>, t: Throwable) {
                            Toast.makeText(this@MobileRegisterScreen, "error", Toast.LENGTH_SHORT)
                                .show()

                        }
                    })

//mobile number

                } else {
                    Toast.makeText(this, "Mobile no should be 10 digit", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_SHORT).show()

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
//                    if (editTexts[3].text.toString().isNotEmpty()){
//
//                    }

                    otp = otp + editTexts[i]

                    Log.d("TAGGGGGGG", "onTextChanged: $otp is the otp")
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