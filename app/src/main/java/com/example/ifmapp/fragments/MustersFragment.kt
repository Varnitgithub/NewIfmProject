package com.example.ifmapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.ifmapp.R
import com.example.ifmapp.databinding.FragmentMustersBinding
import java.text.SimpleDateFormat
import java.time.Month
import java.util.Calendar
import java.util.Locale


class MustersFragment : Fragment() {
    private lateinit var binding: FragmentMustersBinding
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendar = Calendar.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_musters, container, false)

        updateCalendar()

        binding.previousMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        binding.nextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }

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

        // Update the month and year TextView
        val month = calendar.get(Calendar.MONTH).toString()
        val year = calendar.get(Calendar.YEAR).toString()

        var monthName = getMonthName(month.toInt())
        binding.monthTxt.text = "$monthName ${year}"

        // Use the custom adapter
        val adapter = CustomGridAdapter(requireContext(), android.R.layout.simple_list_item_1, dates)
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



    class CustomGridAdapter(context: Context, resource: Int, objects: List<String>) :
        ArrayAdapter<String>(context, resource, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val textView = super.getView(position, convertView, parent) as TextView

            // Check if the item is a day of the week
            val isDayOfWeek = position < 7

            // Make days of the week bold
            if (isDayOfWeek) {
                textView.setTypeface(null, Typeface.BOLD)
            } else {
                textView.setTypeface(null, Typeface.NORMAL)
            }

            // Center-align the text
            textView.gravity = Gravity.CENTER_HORIZONTAL

            // Center-align the text vertically
            textView.setPadding(0, textView.paddingTop, 0, textView.paddingBottom)
            // Set top margin
            if (position >= 7) { // Skip setting margin for days of the week
                val layoutParams = textView.layoutParams

                if (layoutParams is LinearLayout.LayoutParams) {
                    layoutParams.setMargins(0, 1, 0, 0) // Adjust the margin as needed
                    textView.layoutParams = layoutParams
                }
            }


            return textView
        }
    }


}