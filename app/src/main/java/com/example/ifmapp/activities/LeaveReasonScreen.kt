package com.example.ifmapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityLeaveScreen2Binding
import com.example.ifmapp.databinding.ActivityLeaveScreenBinding
import com.example.ifmapp.toast.CustomToast

class LeaveReasonScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLeaveScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_leave_screen)

        binding  =DataBindingUtil.setContentView(this,R.layout.activity_leave_screen)



        binding.btnContinue.setOnClickListener {
           if ( binding.sickCheck.isChecked||binding.familyCheck.isChecked||binding.otherCheck.isChecked){
               startActivity(Intent(this,LeaveDateScreen::class.java))
           }else{
              CustomToast.showToast(this@LeaveReasonScreen,"Please select an option")
           }
        }

        binding.reasonEdt.visibility = View.GONE
        binding.sickCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.familyCheck.isChecked = false
                binding.otherCheck.isChecked  =false
                binding.btnContinue.setBackgroundResource(R.drawable.button_back)
                binding.btnContinue.setTextColor(resources.getColor(R.color.white))
                binding.reasonEdt.visibility = View.GONE
            }
        }

        binding.familyCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.sickCheck.isChecked = false
                binding.otherCheck.isChecked  =false
                binding.btnContinue.setBackgroundResource(R.drawable.button_back)
                binding.btnContinue.setTextColor(resources.getColor(R.color.white))
                binding.reasonEdt.visibility = View.GONE
            }
        }

        binding.otherCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.sickCheck.isChecked  =false
                binding.familyCheck.isChecked = false
                binding.btnContinue.setBackgroundResource(R.drawable.button_back)
                binding.btnContinue.setTextColor(resources.getColor(R.color.white))
                binding.reasonEdt.visibility = View.VISIBLE

            }
        }
    }
}