package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityLeaveScreen2Binding
import com.example.ifmapp.databinding.ActivityLeaveScreenBinding

class LeaveScreen : AppCompatActivity() {

    private lateinit var binding: ActivityLeaveScreen2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    setContentView(R.layout.activity_leave_screen2)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_leave_screen2)

        binding.applyforLeave.setOnClickListener {
            val intent = Intent(this, LeaveReasonScreen::class.java)
            intent.putExtra("leave", "Apply For Leave")
            startActivity(intent)
        }
        binding.leaveAppStatus.setOnClickListener {
            val intent = Intent(this, LeaveReasonScreen::class.java)
            intent.putExtra("leave", "Leave Application Status")
            startActivity(intent)
        }

        binding.leaveRecords.setOnClickListener {
            val intent = Intent(this, LeaveReasonScreen::class.java)
            intent.putExtra("leave", "Leave Records")
            startActivity(intent)
        }
    }
}