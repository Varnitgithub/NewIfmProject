package com.example.ifmapp.databasedb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("EmployeePin")
data class EmployeePin(@PrimaryKey(autoGenerate = true)var id:Int? = null,var mobileNumber:String?=null,var PIN:String?=null,var name:String?=null)
