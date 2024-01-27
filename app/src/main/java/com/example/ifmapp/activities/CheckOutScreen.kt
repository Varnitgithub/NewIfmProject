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
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.shared_preference.shared_preference_models.CheckOutModel
import com.example.ifmapp.shared_preference.shared_preference_models.CurrentUserShiftsDetails
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CheckOutScreen : AppCompatActivity() {
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
    private var finalData: CheckOutModel? = null
    private var finalSiteSelectionData: CurrentUserShiftsDetails? = null
    private var shiftTimingList:List<String>?=null
    private var shiftStartTiming:String?=null
    private var shiftEndTiming:String?=null

    private lateinit var binding: ActivityCheckOutScreenBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //   setContentView(R.layout.activity_check_out_screen)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out_screen)

        employeePinDao = EmployeeDB.getInstance(this).employeePinDao()

        finalData = SaveUsersInSharedPreference.getCurrentUserFinalCheckoutShifts(this)[0]
        finalSiteSelectionData = SaveUsersInSharedPreference.getCurrentUserShifts(this)[0]

        currentTime = finalData?.currentTime
        employeeName = finalData?.employeeName
        address = finalData?.address
        siteSelect = finalSiteSelectionData?.site
        shiftSelect = finalSiteSelectionData?.shift
        shiftTimingList = finalSiteSelectionData?.shiftTiming

        binding.shifts.text = finalSiteSelectionData?.shift

        binding.userName.text = finalSiteSelectionData?.empName

        binding.designation.text = finalSiteSelectionData?.empDesignation

        binding.currentSiteTxt.text = siteSelect

        for (i in 0 until shiftTimingList?.size!!){
            if (shiftSelect.toString()== shiftTimingList!![i].substring(0,1)){
                shiftStartTiming = shiftTimingList!![i].substring(4,9)
                shiftEndTiming = shiftTimingList!![i].substring(10,15)
                binding.shiftStartTime.text = shiftStartTiming
                binding.shiftEndTime.text = shiftEndTiming
            }
        }

        binding.shiftStartdateText.text = getFormattedDate()
        binding.shiftEnddateText.text = getFormattedDate()
        binding.join.text = getFormattedDate()
        binding.out.text = getFormattedDate()

        binding.checkInBtn.isEnabled = false

        binding.checkoutBtn.setOnClickListener {
            startActivity(Intent(this, CheckInScreen::class.java))
        }
        binding.joiningTime.text = currentTime?.substring(0, 5)
         joinedTime = shiftTime?.substring(4,9)
          endTime = shiftTime?.substring(10,16)
    Log.d(
    "TAGGGGG",
    "onCreate: $joinedTime is joined time $endTime is end time $currentTime is current time"
    )

}


    private fun getFormattedDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEE d MMM", Locale.getDefault())
        return dateFormat.format(currentDate)
    }
}