package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityMobileRegisterScreenBinding

class MobileRegisterScreen : AppCompatActivity() {
    private lateinit var binding: ActivityMobileRegisterScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_mobile_register_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile_register_screen)

        binding.wayToSignup.setOnClickListener {
            startActivity(Intent(this, SignUpScreen::class.java))
        }

        binding.btnContinue.setOnClickListener {
            if (binding.mobileNoEdt.text.isNotEmpty()) {
                if (binding.mobileNoEdt.text.toString().toInt() == 10) {


//mobile number

                } else {
                    Toast.makeText(this, "Mobile no should be 10 digit", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_SHORT).show()

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
        }
    }
}