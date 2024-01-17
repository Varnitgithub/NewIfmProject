package com.example.ifmapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.ifmapp.R
import com.example.ifmapp.databinding.FragmentSingleLeaveBinding
import com.example.ifmapp.utils.CommonCalendarUtils
import com.example.ifmapp.utils.CustomGridAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class SingleLeaveFragment : Fragment(), CommonCalendarUtils.CalendarUpdateCallback  {

    private lateinit var binding:FragmentSingleLeaveBinding
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendar = Calendar.getInstance()
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_single_leave, container, false)

        if (::calendar.isInitialized) {
            calendar = Calendar.getInstance()
            updateCalendar()
        }


        checkCalendar()
    return binding.root
    }

    private fun updateCalendar() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthYearString = dateFormat.format(calendar.time)

        (activity as? AppCompatActivity)?.supportActionBar?.title = monthYearString

        val daysOfWeek = arrayListOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
        val dates = arrayListOf<String>()

        // Add days of the week as the first row
        dates.addAll(daysOfWeek)

        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Add empty cells before the first day of the month
        for (i in 1 until firstDayOfMonth) {
            dates.add("")
        }

        // Add days of the month
        for (i in 1..maxDaysInMonth) {
            dates.add(i.toString())
        }

        val adapter = CustomGridAdapter(
            requireContext(),
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

    override fun updatesCalendar(calendar: Calendar) {
        this.calendar = calendar
        updateCalendar()
    }
    fun getCalendar():Calendar{
        return this.calendar
    }

    //kotlin.UninitializedPropertyAccessException: lateinit property calendar has not been initialized
    private fun checkCalendar() {
        if (isAdded &&::binding.isInitialized) {
            updateCalendar()

        }}

}