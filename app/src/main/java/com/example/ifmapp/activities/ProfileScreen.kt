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

class ProfileScreen : AppCompatActivity() {
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
       empName  = intent.getStringExtra("empName")


        val usersList = SaveUsersInSharedPreference.getList(this@ProfileScreen)

        for (user in usersList){
            if (user.pin==pin&&user.userName==empName){
                empName = user.userName
                empDesignation = user.designation
                binding.employeeDesignation.text = empDesignation
                binding.employeeName.text = user.userName
            }
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this@ProfileScreen,MainActivity::class.java)
        intent.putExtra("mPIN",pin)
        intent.putExtra("empName",empName)
        startActivity(intent)
        super.onBackPressed()
    }
}