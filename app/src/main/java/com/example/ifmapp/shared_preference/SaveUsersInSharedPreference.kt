package com.example.ifmapp.shared_preference

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.ifmapp.modelclasses.usermodel_sharedpreference.UserListModel
import com.example.ifmapp.shared_preference.shared_preference_models.CheckOutModel
import com.example.ifmapp.shared_preference.shared_preference_models.CurrentUserShiftsDetails
import com.google.gson.Gson
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken

class SaveUsersInSharedPreference {
    companion object {
        private const val PREF_NAME = "USERS"
        private const val KEY_MY_LIST = "user_list"
        private const val KEY_CURRENT_USER_SHIFT_DETAILS = "current_user_shift_details"
        private const val KEY_FINAL_CHECKOUT_DETAILS = "current_user_final_checkout_details"
        private fun saveList(context: Context, myList: List<UserListModel>) {
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()

            val gson = Gson()
            val json: String = gson.toJson(myList)

            editor.putString(KEY_MY_LIST, json)
            editor.apply()
        }

        private fun isPinExists(context: Context, pin: String,userName:String): Boolean {
            val userList: List<UserListModel> = getList(context)

            for (user in userList) {
                Log.d("TAGGGGGGGG", "isPinExists: ${user.pin}==${pin}")
                if (user.pin == pin && user.userName == userName) {
                    return false
                }
            }
                Log.d("TAGGGGGGGG", "isPinExists: not exists")
                return true
        }

        fun addUserIfNotExists(context: Context,  user: UserListModel,pin:String,userName:String) {
            if (isPinExists(context, pin,userName)) {
                val userList: MutableList<UserListModel> = getList(context).toMutableList()
                Log.d("TAGGGGGGGGGG", "addUserIfNotExists: user not added ${userList.size}")

                userList.add(user)
                Log.d("TAGGGGGGGGGG", "addUserIfNotExists: user added ${userList.size}")
                saveList(context, userList)
            }else{
                Log.d("TAGGGGGGGG", "addUserIfNotExists: this user is not exists")
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

        fun removeUserByPin(context: Context, pinToRemove: String) {
            val userList: MutableList<UserListModel> = getList(context).toMutableList()

            val iterator = userList.iterator()
            while (iterator.hasNext()) {
                val user = iterator.next()
                if (user.pin == pinToRemove) {
                    iterator.remove()
                    saveList(context, userList)
                    break // Assuming each PIN is unique, we can break after removing the user.
                }
            }
        }

          fun saveCurrentUserShifts(context: Context, myList: List<CurrentUserShiftsDetails>) {
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()

            val gson = Gson()
            val json: String = gson.toJson(myList)

            editor.putString(KEY_CURRENT_USER_SHIFT_DETAILS, json)
            editor.apply()
        }

        fun getCurrentUserShifts(context: Context,userName:String): List<CurrentUserShiftsDetails> {
            val preferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json: String? = preferences.getString(KEY_CURRENT_USER_SHIFT_DETAILS, "")

            val gson = Gson()
            val type: Type = object : TypeToken<List<CurrentUserShiftsDetails>>() {}.type
            return gson.fromJson(json, type) ?: ArrayList()
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
            var attendanceStatus = preferences.getString("attendanceStatus","")
            return attendanceStatus
        }
        fun clearAllUsers(){

        }

    }
}

//