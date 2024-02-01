package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.ifmapp.R

class LoginCheckedScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_checked_screen)

        val loginByPhone: Button = findViewById(R.id.loginByPhoneAndPin)
        var loginByCompanyCode: Button = findViewById(R.id.loginByCompanyCode)
     //   var forgotPin: Button = findViewById(R.id.forgotPin)

        loginByPhone.setOnClickListener {
            startActivity(Intent(this, LoginByPinMobileScreen::class.java))
        }
        loginByCompanyCode.setOnClickListener {
            startActivity(Intent(this, SignInScreen::class.java))
        }



    }
}