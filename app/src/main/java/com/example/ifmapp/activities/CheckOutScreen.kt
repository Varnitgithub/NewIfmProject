package com.example.ifmapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityCheckOutScreenBinding
import com.example.ifmapp.shared_preference.SaveUsersInSharedPreference
import com.example.ifmapp.shared_preference.shared_preference_models.CheckOutModel
import com.example.ifmapp.shared_preference.shared_preference_models.CurrentUserShiftsDetails
import java.text.SimpleDateFormat
import java.util.Calendar
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
    private var shiftSelect: String? = null
    private var finalData: CheckOutModel? = null
    private var finalSiteSelectionData: CurrentUserShiftsDetails? = null
    private var shiftTimingList: List<String>? = null
    private var shiftStartTiming: String? = null
    private var shiftEndTiming: String? = null
    private var outTiming: String? = null

    private lateinit var binding: ActivityCheckOutScreenBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //   setContentView(R.layout.activity_check_out_screen)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out_screen)


        finalData = SaveUsersInSharedPreference.getCurrentUserFinalCheckoutShifts(this)[0]
       //finalSiteSelectionData = SaveUsersInSharedPreference.getCurrentUserShifts(this,muse)[0]
        outTiming = intent.getStringExtra("inoutStatus")
        Log.d("TAGGGGGGGGGG", "onCreate: $outTiming is out timing")
        if (outTiming.toString().isNotEmpty()&&outTiming.toString()=="IN"){

            if (getCurrentTime().substring(0,2).toInt()<12){
                binding.joiningTime.text = getCurrentTime()
                binding.joiningAm.text = "AM"
                SaveUsersInSharedPreference.setShiftJoiningTime(this@CheckOutScreen
                    , getCurrentTime(),"AM"
                )
            }else{
                binding.joiningTime.text = getCurrentTime()
                binding.joiningAm.text = "PM"
                SaveUsersInSharedPreference.setShiftJoiningTime(this@CheckOutScreen
                    , getCurrentTime(),"PM"
                )
            }

        }else{
            if (getCurrentTime().substring(0,2).toInt()<12){
                binding.outTime.text = getCurrentTime()
                binding.outPm.text = "AM"
                val _InTime= SaveUsersInSharedPreference.getShiftJoiningTime(this)
                binding.joiningTime.text = _InTime.toString()
            }else{
                binding.outTime.text = getCurrentTime()
                binding.outPm.text = "PM"
                val _InTime= SaveUsersInSharedPreference.getShiftJoiningTime(this)
                binding.joiningTime.text = _InTime.toString()
            }


        }

        for (i in 0 until shiftTimingList?.size!!) {
            if (shiftSelect.toString() == shiftTimingList!![i].substring(0, 1)) {
                shiftStartTiming = shiftTimingList!![i].substring(4, 9)
                shiftEndTiming = shiftTimingList!![i].substring(10, 15)
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
            val intent = Intent(this@CheckOutScreen, CheckInScreen::class.java)
            intent.putExtra("INOUTStatus", "OUT")
            startActivity(intent)
        }
    }
    private fun getFormattedDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEE d MMM", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@CheckOutScreen, DashBoardScreen::class.java))
    }

    fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(currentTime)
    }
}