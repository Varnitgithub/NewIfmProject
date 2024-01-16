package com.example.ifmapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityLeaveScreenBinding

class LeaveScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLeaveScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_leave_screen)

        binding  =DataBindingUtil.setContentView(this,R.layout.activity_leave_screen)

        binding.sickCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                println("Option 1 selected in real-time")
            }
        }

        binding.familyCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                println("Option 2 selected in real-time")
            }
        }

        binding.otherCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                println("Option 3 selected in real-time")
            }
        }
    }
}