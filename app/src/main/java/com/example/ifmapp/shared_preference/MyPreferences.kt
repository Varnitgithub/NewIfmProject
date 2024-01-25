package com.example.ifmapp.shared_preference

import android.content.Context
import android.content.SharedPreferences

class MyPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveUserData(name: String, mobileNumber: String, pin: String,locationAutoId:String) {
        with(sharedPreferences.edit()) {
            putString("NAME", name)
            putString("MOBILE_NUMBER", mobileNumber)
            putString("PIN", pin)
            putString("LOCATION_AUTOID", pin)
            apply()
        }

    }

    fun readUserData(): Triple<String?, String?, String?> {
        val name = sharedPreferences.getString("NAME", null)
        val mobileNumber = sharedPreferences.getString("MOBILE_NUMBER", null)
        val pin = sharedPreferences.getString("PIN", null)

        return Triple(name, mobileNumber, pin)
    }
}
