package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.ifmapp.R

class RegistrationScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_screen)

        val newRegistration: Button = findViewById(R.id.new_Registration)
        val alreadyRegistered: Button = findViewById(R.id.already_Registered)

        newRegistration.setOnClickListener {
            startActivity(Intent(this,SignUpWaysScreen::class.java))
        }
        alreadyRegistered.setOnClickListener {
            startActivity(Intent(this,LoginCheckedScreen::class.java))
        }
    }
}