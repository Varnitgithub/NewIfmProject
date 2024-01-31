package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.MainActivity
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityLeaveScreen2Binding
import com.example.ifmapp.databinding.ActivityProfileScreenBinding
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.shared_preference.shared_preference_models.CurrentUserShiftsDetails

class ProfileScreen() : AppCompatActivity() {
    private lateinit var binding: ActivityProfileScreenBinding
    private var empName:String?=null
    private var empDesignation:String?=null
    private var empDetails:UserListModel?=null
    private var pin:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_profile_screen)
binding = DataBindingUtil.setContentView(this,R.layout.activity_profile_screen)

        pin = intent.getStringExtra("mPIN")

        Log.d("TAGGGGGGGGGGGG", "onCreate: $pin is the profile screen otp")
      //   empDetails = SaveUsersInSharedPreference.getUserByPin(this@ProfileScreen, pin = pin.toString())
        Log.d("TAGGGGGGGGGGGG", "onCreate: $empDetails is the profile screen otp")

        empName = empDetails?.userName
        empDesignation = empDetails?.designation

        binding.employeeName.text = empName
        binding.employeeDesignation.text = empDesignation

    }

    override fun onBackPressed() {
        val intent = Intent(this@ProfileScreen,MainActivity::class.java)
        intent.putExtra("mPIN",pin)
        startActivity(intent)
        super.onBackPressed()
    }
}