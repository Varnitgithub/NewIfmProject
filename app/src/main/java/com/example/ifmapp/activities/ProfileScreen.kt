package com.example.ifmapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityLeaveScreen2Binding
import com.example.ifmapp.databinding.ActivityProfileScreenBinding
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.shared_preference.shared_preference_models.CurrentUserShiftsDetails

class ProfileScreen : AppCompatActivity() {
    private lateinit var binding: ActivityProfileScreenBinding
    private var empName:String?=null
    private var empDesignation:String?=null
    private var empDetails:CurrentUserShiftsDetails?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_profile_screen)
binding = DataBindingUtil.setContentView(this,R.layout.activity_profile_screen)

         empDetails = SaveUsersInSharedPreference.getCurrentUserShifts(this@ProfileScreen)[0]
        empName = empDetails?.empName
        empDesignation = empDetails?.empDesignation

        binding.employeeName.text = empName
        binding.employeeDesignation.text = empDesignation

    }
}