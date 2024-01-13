package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityMobileRegisterScreenBinding

class MobileRegisterScreen : AppCompatActivity() {
    private lateinit var binding: ActivityMobileRegisterScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_mobile_register_screen)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_mobile_register_screen)

        binding.wayToSignup.setOnClickListener {
            startActivity(Intent(this,SignUpScreen::class.java))
        }
    }
}