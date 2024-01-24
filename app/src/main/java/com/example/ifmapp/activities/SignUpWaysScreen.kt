package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.ifmapp.R

class SignUpWaysScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_ways_screen)

        val signupMobile: Button = findViewById(R.id.signup_Mobile)
        val signupCompanyCode: Button = findViewById(R.id.signup_CompanyCode)

        signupMobile.setOnClickListener {
            startActivity(Intent(this,MobileRegisterScreen::class.java))
        }
        signupCompanyCode.setOnClickListener {
            startActivity(Intent(this,SignUpWithoutMobile::class.java))
        }
    }
}