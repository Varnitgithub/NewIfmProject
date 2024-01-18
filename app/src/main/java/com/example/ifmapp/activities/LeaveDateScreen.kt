package com.example.ifmapp.activities

import CustomDialogClass
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityLeaveDateScreenBinding
import com.example.ifmapp.fragments.LongLeaveFragment
import com.example.ifmapp.fragments.MultipleLeaveFragment
import com.example.ifmapp.fragments.SingleLeaveFragment
import com.example.ifmapp.utils.CommonCalendarUtils
import java.util.Calendar

class LeaveDateScreen : AppCompatActivity() {
    private val singleLeaveFragment by lazy { SingleLeaveFragment(this) }
    private val multipleLeaveFragment by lazy { MultipleLeaveFragment(this) }
    private val longleaveFragment by lazy { LongLeaveFragment(this) }
    private lateinit var calendar: Calendar
    private lateinit var customDialog :CustomDialogClass


    private lateinit var binding: ActivityLeaveDateScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_leave_date_screen)
        // Default fragment
        calendar = Calendar.getInstance()
       // longLeaveFragment.setCalendarUpdateListener(this)
        customDialog = CustomDialogClass(this)
        addFragment(singleLeaveFragment)


        binding.btnLeaveApprove.setOnClickListener {

            dialog()

        }

        binding.singleLeave.setOnClickListener {
            updateButtonUI(binding.singleLeave)
            addFragment(singleLeaveFragment)
        }

        binding.multipleLeave.setOnClickListener {
            updateButtonUI(binding.multipleLeave)
            addFragment(multipleLeaveFragment)
        }

        binding.longLeave.setOnClickListener {
            updateButtonUI(binding.longLeave)
            addFragment(longleaveFragment)

        }

    }


    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.leaveFrameLayout, fragment)
            .commit()
    }

    private fun updateButtonUI(selectedButton: Button) {
        // Reset all buttons to default state
        resetButtonUI(binding.singleLeave)
        resetButtonUI(binding.multipleLeave)
        resetButtonUI(binding.longLeave)

        // Update the selected button's UI
        selectedButton.setBackgroundResource(R.drawable.button_back)
        selectedButton.setTextColor(resources.getColor(R.color.white))
    }

    private fun resetButtonUI(button: Button ) {
        button.setBackgroundResource(R.drawable.site_selection_back)
        button.setTextColor(resources.getColor(R.color.btn_continue))
    }


    private fun dialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.leave_approve_dialog)

        val doneBtn: Button = dialog.findViewById(R.id.done_btn)
        val crossDialog: ImageView = dialog.findViewById(R.id.cross_leaveDialog)

        doneBtn.setOnClickListener {
            dialog.dismiss()
        }
        crossDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
