package com.example.ifmapp.utils

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView

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