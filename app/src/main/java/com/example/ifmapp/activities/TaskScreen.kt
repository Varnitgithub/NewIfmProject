package com.example.ifmapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.ActivityTaskScreenBinding
import com.example.ifmapp.utils.CustomGridAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskScreen : AppCompatActivity() {
    private lateinit var binding:ActivityTaskScreenBinding
    private lateinit var calendar: Calendar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_screen)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_task_screen)
        calendar = Calendar.getInstance()


        updateCalendar()

        binding.previousMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        binding.nextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }
    }

    private fun updateCalendar() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthYearString = dateFormat.format(calendar.time)

        supportActionBar?.title = monthYearString

        val daysOfWeek = arrayListOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
        val dates = arrayListOf<String>()

        // Add days of the week as the first row
        dates.addAll(daysOfWeek)

        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val txtYear = calendar.get(Calendar.YEAR)
        val txtMonth = calendar.get(Calendar.MONTH)

        binding.monthTxt.text =  "${getMonthName(txtMonth)} $txtYear"

        // Add empty cells before the first day of the month
        for (i in 1 until firstDayOfMonth) {
            dates.add("")
        }

        // Add days of the month
        for (i in 1..maxDaysInMonth) {
            dates.add(i.toString())
        }

        val adapter = CustomGridAdapter(
            this,
            android.R.layout.simple_list_item_1,
            dates
        )
        binding.gridView.adapter = adapter
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

    fun getMonth(): Int {
        return calendar.get(Calendar.MONTH)
    }

    fun getYear(): Int {
        return calendar.get(Calendar.YEAR)
    }


    fun getCalendar(): Calendar {
        return this.calendar
    }

    //kotlin.UninitializedPropertyAccessException: lateinit property calendar has not been initialized
    private fun checkCalendar() {
        if ( ::binding.isInitialized) {
            updateCalendar()

        }}
}