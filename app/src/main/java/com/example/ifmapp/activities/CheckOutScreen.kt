package com.example.ifmapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databasedb.EmployeeDB
import com.example.ifmapp.databasedb.EmployeePinDao
import com.example.ifmapp.databinding.ActivityCheckOutScreenBinding
import com.example.ifmapp.fragments.BottomFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CheckOutScreen : AppCompatActivity() {
    private lateinit var gestureDetector: GestureDetectorCompat
    private var shiftTime: String? = null
    private var currentTime: String? = null
    private var endTime: String? = null
    private var joinedTime: String? = null
    private var employeeName: String? = null
    private var employeeDesignation: String? = null
    private var siteSelect: String? = null
    private var address: String? = null
    private lateinit var employeePinDao: EmployeePinDao
    private var shiftSelect: String? = null

    private lateinit var binding: ActivityCheckOutScreenBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //   setContentView(R.layout.activity_check_out_screen)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out_screen)

        employeePinDao = EmployeeDB.getInstance(this).employeePinDao()
        /*gestureDetector = GestureDetectorCompat(this, SwipeGestureListener())

            binding.frameLayout.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
            }*/

        currentTime = intent.getStringExtra("currentTime")
        employeeName = intent.getStringExtra("employeeName")
        address = intent.getStringExtra("address")
        siteSelect = intent.getStringExtra("siteSelect")
        shiftSelect = intent.getStringExtra("shiftSelect")

binding.shiftStartdateText.text = getFormattedDate()
binding.shiftEnddateText.text = getFormattedDate()
binding.join.text = getFormattedDate()
binding.out.text = getFormattedDate()

        employeePinDao.getShiftTimingDetails().observe(this) {
            Log.d(
                "TAGGGGGGGGGGGG",
                "onCreate: ${it.shiftCode} and ${it.shiftTiming} is shift code and shift timing"

            )
            binding.shifts.text = it.shiftCode
            binding.shiftStartTime.text = it.shiftTiming?.substring(4,9)
            binding.shiftEndTime.text = it.shiftTiming?.substring(10,15)
        }
        binding.checkInBtn.isEnabled = false
//        val bottomFragment = BottomFragment.newInstance()
//        bottomFragment.show(supportFragmentManager, "add_photo_dialog_fragment")

        binding.checkoutBtn.setOnClickListener {

            startActivity(Intent(this, CheckInScreen::class.java))
        }
        binding.joiningTime.text = currentTime?.substring(0, 5)
        if (currentTime?.substring(0, 5).toString().substring(0,2).toInt()<12){
            binding.joiningAm.text = "AM"
            Log.d("TAGGGGGG", "onCreate: this is am")
        }else{
            binding.joiningAm.text = "PM"
            Log.d("TAGGGGGGG", "onCreate: this is pm")

        }

//          joinedTime = shiftTime?.substring(4,9)
//          endTime = shiftTime?.substring(10,16)

        Log.d(
            "TAGGGGG",
            "onCreate: $joinedTime is joined time $endTime is end time $currentTime is current time"
        )


       /* binding.shiftStartTime.text = joinedTime

        binding.shiftEndTime.text = endTime*/

        binding.userName.text = employeeName
        binding.designation.text = employeeDesignation
        //binding.currentSiteTxt.text = siteSelect
        //  binding.companyTxt.text = address
        // binding.shifts.text = shiftSelect?.substring(3,15)
        //  binding.outTime.text =
    }


    private inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val deltaY = e2.y.minus(e1?.y ?: 0F) ?: 0F

            // Check if the swipe is from bottom to top
            if (deltaY < 0) {
                // Show the bottom sheet fragment
//                val bottomFragment = BottomFragment.newInstance()
//                bottomFragment.show(supportFragmentManager, "bottom_fragment")
            }

            // Return true to indicate that the event has been consumed
            return true
        }
    }

    private fun getFormattedDate(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
        val dayOfMonth = SimpleDateFormat("d", Locale.getDefault()).format(date)
        val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date)
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)

        return "$dayOfWeek $dayOfMonth $month $year"
    }
    private fun getFormattedDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEE d MMM", Locale.getDefault())
        return dateFormat.format(currentDate)
    }
}