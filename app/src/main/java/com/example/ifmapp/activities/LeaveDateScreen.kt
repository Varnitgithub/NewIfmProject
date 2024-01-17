package com.example.ifmapp.activities

import CustomDialogClass
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
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

class LeaveDateScreen : AppCompatActivity(), CommonCalendarUtils.CalendarUpdateCallback {
    private val singleLeaveFragment by lazy { SingleLeaveFragment() }
    private val multipleLeaveFragment by lazy { MultipleLeaveFragment() }
    private val longleaveFragment by lazy { LongLeaveFragment() }
    private lateinit var calendar: Calendar
    private lateinit var customDialog :CustomDialogClass
    private   var commonCalendarUtils: CommonCalendarUtils= CommonCalendarUtils(this)


    private lateinit var binding: ActivityLeaveDateScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_leave_date_screen)
        // Default fragment
        calendar = Calendar.getInstance()
       // longLeaveFragment.setCalendarUpdateListener(this)
        customDialog = CustomDialogClass(this)
        addFragment(singleLeaveFragment)
        accessMonthAndYear()
        // Button click listeners
        binding.singleLeave.setOnClickListener {
            updateButtonUI(binding.singleLeave)
            addFragment(singleLeaveFragment)
        }

        binding.btnLeaveApprove.setOnClickListener {

          showCustomDialog()

        }

        binding.multipleLeave.setOnClickListener {
            updateButtonUI(binding.multipleLeave)
            addFragment(multipleLeaveFragment)
        }

        binding.longLeave.setOnClickListener {
            updateButtonUI(binding.longLeave)
            addFragment(longleaveFragment)

//            longLeaveFragment.setCalendarUpdateListener(this)
//            addFragment(longLeaveFragment)

            // Call both updatePreviousMonth and updateNextMonth

        }

        // Initialize LongLeaveFragment with listener


        binding.previousMonth.setOnClickListener {
            val frameLayout = findViewById<FrameLayout>(R.id.leaveFrameLayout)
            val currentFragment = supportFragmentManager.findFragmentById(frameLayout.id)

            if (currentFragment is SingleLeaveFragment) {
                commonCalendarUtils.updatePreviousMonth(singleLeaveFragment.getCalendar())
                accessMonthAndYear()
            } else if (currentFragment is MultipleLeaveFragment) {
                commonCalendarUtils.updatePreviousMonth(multipleLeaveFragment.getCalendar())
                accessMonthAndYear()
            } else if (currentFragment is LongLeaveFragment) {
                commonCalendarUtils.updatePreviousMonth(longleaveFragment.getCalendar())
                accessMonthAndYear()
            } else {
                Toast.makeText(this, "no fragment attached", Toast.LENGTH_SHORT).show()
            }

        }

        binding.nextMonth.setOnClickListener {
            val frameLayout = findViewById<FrameLayout>(R.id.leaveFrameLayout)
            val currentFragment = supportFragmentManager.findFragmentById(frameLayout.id)

            if (currentFragment is SingleLeaveFragment) {
                commonCalendarUtils.updateNextMonth(singleLeaveFragment.getCalendar())
                accessMonthAndYear()
            } else if (currentFragment is MultipleLeaveFragment) {
                commonCalendarUtils.updateNextMonth(multipleLeaveFragment.getCalendar())
                accessMonthAndYear()
            } else if (currentFragment is LongLeaveFragment) {
                commonCalendarUtils.updateNextMonth(longleaveFragment.getCalendar())
                accessMonthAndYear()
            } else {
                Toast.makeText(this, "no fragment attached", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun updatesCalendar(calendar: Calendar) {
        longleaveFragment.updatesCalendar(calendar)
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
      //  button.setTextColor(resources.getColor(R.color.btn_continue))
    }



    private fun accessMonthAndYear() {
        val longLeaveFragment = supportFragmentManager.findFragmentById(R.id.leaveFrameLayout) as? LongLeaveFragment

        val month = longLeaveFragment?.getMonth()
        val year = longLeaveFragment?.getYear()

        // Now you can use 'month' and 'year' as needed
        // For example, updating a TextView in your activity
       // binding.monthTxt.text = "${month?.let { getMonthName(it) }} $year"
    }

    private fun getMonthName(monthNumber: Int): String {
        val monthNames = arrayOf(
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"
        )

        return if (monthNumber in 0..11) {
            monthNames[monthNumber]
        } else {
            "Invalid month number"
        }
    }
    private fun showCustomDialog() {
        val dialog = Dialog(this)
        val inflater = LayoutInflater.from(this)
        val customDialogView = inflater.inflate(R.layout.leave_approve_dialog, null)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setLayout(layoutParams.width, layoutParams.height)

        dialog.setContentView(customDialogView)
        dialog.show()
    }
}
