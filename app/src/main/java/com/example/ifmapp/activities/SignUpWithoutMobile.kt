package com.example.ifmapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivitySignUpWithoutMobileBinding

class SignUpWithoutMobile : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpWithoutMobileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_sign_up_without_mobile)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_without_mobile)


        binding.employeepinEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.companycodeEdt.text.toString()
                        .isNotEmpty() && binding.employeeidEdt.text.toString().length ==4&&
                    binding.employeepinEdt.text.toString().length ==4
                ) {
                    binding.btnGenerate.setBackgroundResource(R.drawable.button_back)
                binding.btnGenerate.setTextColor(resources.getColor(R.color.white))
            } else {
                binding.btnGenerate.setBackgroundResource(R.drawable.site_selection_back)
                binding.btnGenerate.setTextColor(resources.getColor(R.color.btn_continue))



                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })


    }

    fun signUpUser(companyCode:String,employeeId:String,employeePin:String){


    }


}