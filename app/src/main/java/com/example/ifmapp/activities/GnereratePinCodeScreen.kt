package com.example.ifmapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityEnterPinCodeScreenBinding
import com.example.ifmapp.databinding.ActivityGnereratePinCodeScreenBinding

class GnereratePinCodeScreen : AppCompatActivity() {
    private lateinit var edtPincodeLiveData: MutableLiveData<Int>
    private lateinit var binding: ActivityGnereratePinCodeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_enter_pin_code_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gnererate_pin_code_screen)
        edtPincodeLiveData = MutableLiveData()

        edtPincodeLiveData.observe(this) {
            if (it >= 5) {
                binding.btnGenerate.setBackgroundResource(R.drawable.button_back)
                binding.btnGenerate.setTextColor(resources.getColor(R.color.white))
            } else {
                binding.btnGenerate.setBackgroundResource(R.drawable.site_selection_back)
                binding.btnGenerate.setTextColor(resources.getColor(R.color.btn_continue))
            }
        }


        binding.edtEnterPinCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentLength = s?.length ?: 0
                edtPincodeLiveData.postValue(currentLength)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }
}