package com.example.ifmapp.databasedb

import androidx.room.Entity
import androidx.room.PrimaryKey

data class EmployeePin(
    var mobileNumber: String? = null,
    var PIN: String? = null
)
