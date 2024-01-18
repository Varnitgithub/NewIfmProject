package com.example.ifmapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.ifmapp.R

class CustomGridAdapter(context: Context, resource: Int, dates: List<String>) :
    ArrayAdapter<String>(context, resource, dates) {

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

        // Set background color based on the date
        setBackgroundColor(position, textView)

        return textView
    }

    // Add this method to set background color for specific dates
    @SuppressLint("ResourceAsColor")
    fun setBackgroundColor(position: Int, textView: TextView) {
        val date = getItem(position)?.toIntOrNull()
         textView.gravity = Gravity.CENTER
        // Check for specific dates and set background color accordingly
        when (date) {

            3, 9, 30 -> {
                textView.setBackgroundResource(R.drawable.duty_date_back)
                textView.setTextColor(R.color.white)
            }
            4, 10, 29 ->  {
                textView.setBackgroundResource(R.drawable.weekly_date_back)
                textView.setTextColor(R.color.white)
            }
            5, 11, 23 ->  {
                textView.setBackgroundResource(R.drawable.unsync_date_back)
                textView.setTextColor(R.color.white)
            }
            6, 12, 24 ->  {
                textView.setBackgroundResource(R.drawable.applied_date_back)
                textView.setTextColor(R.color.white)
            }
            7, 13, 27 ->  {
                textView.setBackgroundResource(R.drawable.approve_date_back)
                textView.setTextColor(R.color.white)
            }
            8, 14, 19 ->  {
                textView.setBackgroundResource(R.drawable.past_date_back)
                textView.setTextColor(R.color.white)
            }
            // Add more cases as needed
            else -> textView.setBackgroundColor(Color.TRANSPARENT) // Default background
        }
    }
}
