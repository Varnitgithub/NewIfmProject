package com.example.ifmapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivitySignUpScreenBinding

class SignUpScreen : AppCompatActivity() {

    private lateinit var employeeidLiveData:MutableLiveData<Int>

    private lateinit var binding: ActivitySignUpScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_screen)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up_screen)
employeeidLiveData = MutableLiveData()

        employeeidLiveData.observe(this){
            if (it>=5){
                binding.btnContinue.setBackgroundResource(R.drawable.button_back)
                binding.btnContinue.setTextColor(resources.getColor(R.color.white))
            }else{
                binding.btnContinue.setBackgroundResource(R.drawable.site_selection_back)
                binding.btnContinue.setTextColor(resources.getColor(R.color.btn_continue))
            }
        }

        binding.employeeId.addTextChangedListener(object : TextWatcher {
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