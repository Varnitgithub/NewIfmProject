package com.example.ifmapp.toast

import android.content.Context
import android.widget.Toast
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import com.example.ifmapp.R

class CustomToast {

    companion object {

        fun showToast(context: Context, message: String) {
            // Create a LayoutInflater object to inflate custom layout
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater.inflate(R.layout.toast_message, null)

            // Set the message in the custom layout
            val textView = layout.findViewById<TextView>(R.id.toast_text)
            textView.text = message

            // Create a Toast object
            val toast = Toast(context)

            // Set the custom layout to the toast
            toast.view = layout

            // Set the duration of the toast
            toast.duration = Toast.LENGTH_SHORT

            // Set the gravity of the toast
            toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)

            // Show the toast
            toast.show()
        }
    }
}
