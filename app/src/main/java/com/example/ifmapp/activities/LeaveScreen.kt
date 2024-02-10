package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityLeaveScreen2Binding
import com.example.ifmapp.databinding.ActivityLeaveScreenBinding
import com.example.ifmapp.utils.UserObject

class LeaveScreen : AppCompatActivity() {

    private lateinit var binding: ActivityLeaveScreen2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    setContentView(R.layout.activity_leave_screen2)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_leave_screen2)

        Log.d("TAGGGGGGG", "onResume: this is user details ${UserObject.userNames} ${UserObject.userId} ${UserObject.designation} ${UserObject.userPin}")


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

    override fun onBackPressed() {
        startActivity(Intent(this@LeaveScreen,MainActivity::class.java))
        super.onBackPressed()
    }


}