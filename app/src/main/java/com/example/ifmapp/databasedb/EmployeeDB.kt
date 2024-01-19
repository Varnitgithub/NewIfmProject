package com.example.ifmapp.databasedb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ifmapp.modelclasses.loginby_pin.LoginByPINResponseItem


@Database(entities = [EmployeePin::class,LoginByPINResponseItem::class], version = 1, exportSchema = false)
abstract class EmployeeDB :RoomDatabase() {

    abstract fun employeePinDao():EmployeePinDao

    companion object{

        @Volatile
        var INSTANCE:EmployeeDB?=null
        fun getInstance(context: Context):EmployeeDB{

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EmployeeDB::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}