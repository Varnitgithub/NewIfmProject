package com.example.ifmapp.shared_preference

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
import com.example.ifmapp.shared_preference.shared_preference_models.CheckOutModel
import com.example.ifmapp.shared_preference.shared_preference_models.CurrentUserShiftsDetails
import com.google.gson.Gson
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken

class SaveUsersInSharedPreference {
    companion object {
        private const val PREF_NAME = "EMPLOYEES"
        private const val KEY_MY_LIST = "USER_LIST"
        private const val KEY_CURRENT_USER_SHIFT_DETAILS = "Current_User_Shift_Detail"
        private const val KEY_FINAL_CHECKOUT_DETAILS = "Current_User_Final_Checkout_Details"
        private fun saveList(context: Context, myList: List<UserListModel>) {
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()

            val gson = Gson()
            val json: String = gson.toJson(myList)

            editor.putString(KEY_MY_LIST, json)
            editor.apply()
        }

        private fun isPinExists(context: Context, pin: String,userId:String): Boolean {
            val userList: List<UserListModel> = getList(context)

            for (user in userList) {
                if (user.pin == pin && user.empId == userId) {
                    return false
                }
            }
                return true
        }

        fun addUserIfNotExists(context: Context,  user: UserListModel,pin:String,userId:String) {
            if (isPinExists(context, pin,userId)) {
                val userList: MutableList<UserListModel> = getList(context).toMutableList()
                userList.add(user)
                saveList(context, userList)
            }else{
            }
        }

          fun getList(context: Context): List<UserListModel> {
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json: String? = preferences.getString(KEY_MY_LIST, "")

            val gson = Gson()
            val type: Type = object : TypeToken<List<UserListModel>>() {}.type
            return gson.fromJson(json, type) ?: ArrayList()
        }

        fun getUserByPin(context: Context, pin: String,name:String): UserListModel? {
            val userList: List<UserListModel> = getList(context)

            for (user in userList) {
                if (user.pin == pin && user.userName==name) {
                    return user
                }
            }

            return null
        }

        fun removeUserByPin(context: Context, userId:String) {
            val userList: MutableList<UserListModel> = getList(context).toMutableList()

            val iterator = userList.iterator()
            while (iterator.hasNext()) {
                val user = iterator.next()
                if (user.empId == userId) {
                    iterator.remove()
                    saveList(context, userList)
                    break // Assuming each PIN is unique, we can break after removing the user.
                }
            }
        }

          fun saveCurrentUserShifts(context: Context, myList: List<CurrentUserShiftsDetails>,userid:String) {
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()

            val gson = Gson()
            val json: String = gson.toJson(myList)

            editor.putString(userid, json)
            editor.apply()
        }

        fun getCurrentUserShifts(context: Context,userId:String): List<CurrentUserShiftsDetails> {
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json: String? = preferences.getString(userId, "")

            val gson = Gson()
            val type: Type = object : TypeToken<List<CurrentUserShiftsDetails>>() {}.type
            return gson.fromJson(json, type) ?: ArrayList()
        }

        fun deleteCurrentUserShifts(context: Context, userId: String) {
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()

            // Remove the entry for the specified user ID
            editor.remove(userId)

            editor.apply()
        }


        fun saveCurrentUserFinalCheckout(context: Context, myList: List<CheckOutModel>) {
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()

            val gson = Gson()
            val json: String = gson.toJson(myList)

            editor.putString(KEY_FINAL_CHECKOUT_DETAILS, json)
            editor.apply()
        }

        fun getCurrentUserFinalCheckoutShifts(context: Context): List<CheckOutModel> {
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json: String? = preferences.getString(KEY_FINAL_CHECKOUT_DETAILS, "")

            val gson = Gson()
            val type: Type = object : TypeToken<List<CheckOutModel>>() {}.type
            return gson.fromJson(json, type) ?: ArrayList()
        }

          fun setShiftJoiningTime(context: Context,time:String,amPm:String){
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("shiftStartTime", time)
            editor.apply()

        }

        fun getShiftJoiningTime(context: Context): String? {
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            var setShiftTime = preferences.getString("shiftStartTime","")
            return setShiftTime
        }

        fun setAttendanceStatus(context: Context,status:String){
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("attendanceStatus", status)
            editor.apply()

        }

        fun getAttendanceStatus(context: Context): String? {
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val attendanceStatus = preferences.getString("attendanceStatus","")
            return attendanceStatus
        }

        private val realTimeUserListLiveData = MutableLiveData<List<UserListModel>>()

        fun getRealTimeUsers(context: Context): LiveData<List<UserListModel>> {
            // Retrieve the initial list
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val initialListJson: String? = preferences.getString(KEY_MY_LIST, "")
            val initialList: List<UserListModel> =
                Gson().fromJson(initialListJson, object : TypeToken<List<UserListModel>>() {}.type)
            realTimeUserListLiveData.value = initialList

            // Observe the SharedPreferences for changes
            val listener =
                SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key == KEY_MY_LIST) {
                        val userList: List<UserListModel> = getList(context)
                        realTimeUserListLiveData.value = userList
                        // Update the list in SharedPreferences
                        preferences.edit()
                            .putString(KEY_MY_LIST, Gson().toJson(userList)).apply()
                    }
                }
            preferences.registerOnSharedPreferenceChangeListener(listener)

            return realTimeUserListLiveData
        }

        // ... (other existing functions)
    }
}


//