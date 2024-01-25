package com.example.ifmapp.activities

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivitySignUpScreenBinding

class SignUpScreen : AppCompatActivity() {

    private lateinit var employeeidLiveData: MutableLiveData<Int>
    private lateinit var otpLiveData: MutableLiveData<Int>

    private lateinit var binding: ActivitySignUpScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_screen)


        binding.btnContinue.setOnClickListener {
               startActivity(Intent(this,GnereratePinCodeScreen::class.java))
//            if (binding.companycodeEdt.text.isNotEmpty()&&binding.employeeidEdt.text.isNotEmpty()
//                &&binding.employeepinEdt.text.isNotEmpty()){
//
//                binding.btnContinue.setBackgroundResource(R.drawable.button_back)
//                binding.btnContinue.setTextColor(resources.getColor(R.color.white))
//
//                //Move forward
//
//
//            }else{
//                binding.btnContinue.setBackgroundResource(R.drawable.site_selection_back)
//                binding.btnContinue.setTextColor(resources.getColor(R.color.btn_continue))
//                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
//            }

        }

        //Initialization
        employeeidLiveData = MutableLiveData()
        otpLiveData = MutableLiveData()



        employeeidLiveData.observe(this) {
            if (it >= 9) {
                binding.btnContinue.setBackgroundResource(R.drawable.button_back)
                binding.btnContinue.setTextColor(resources.getColor(R.color.white))
            } else {
                binding.btnContinue.setBackgroundResource(R.drawable.site_selection_back)
                binding.btnContinue.setTextColor(resources.getColor(R.color.btn_continue))
            }
        }

        otpLiveData.observe(this) {
                binding.btnContinue.setBackgroundResource(R.drawable.button_back)
                binding.btnContinue.setTextColor(resources.getColor(R.color.white))
                binding.btnContinue.setBackgroundResource(R.drawable.site_selection_back)
                binding.btnContinue.setTextColor(resources.getColor(R.color.btn_continue))

        }

        binding.employeeidEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentLength = s?.length ?: 0
                employeeidLiveData.postValue(currentLength)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })



    }









    }
