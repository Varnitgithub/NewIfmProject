package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.ifmapp.R
import com.example.ifmapp.checked
import com.example.ifmapp.databasedb.EmployeeDB
import com.example.ifmapp.databasedb.EmployeePinDao

class RegistrationScreen : AppCompatActivity() {
    private lateinit var employeePinDao:EmployeePinDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        employeePinDao = EmployeeDB.getInstance(this).employeePinDao()
        setContentView(R.layout.activity_registration_screen)
        if (!checked.isChecked) {
            checked.isChecked = true
            checkReceiverData()
        }
        val newRegistration: Button = findViewById(R.id.new_Registration)
        val alreadyRegistered: Button = findViewById(R.id.already_Registered)

        newRegistration.setOnClickListener {
            startActivity(Intent(this,SignUpWaysScreen::class.java))
        }
        alreadyRegistered.setOnClickListener {
            startActivity(Intent(this,LoginCheckedScreen::class.java))
        }
    }
    private fun checkReceiverData() {
        employeePinDao.getAllEmployeeDetails().observe(this@RegistrationScreen) {
            if (it.isNotEmpty()) {
                startActivity(Intent(this@RegistrationScreen, DashBoardScreen::class.java))
            }
        }
    }
}