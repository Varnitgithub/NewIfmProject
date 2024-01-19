package com.example.ifmapp.modelclasses

import androidx.room.Entity

data class EmployeeDetailsItem(
    val CompanyCode: String,
    val IsClusterVisible: String,
    val LocationautoID: String,
    val MessageID: String,
    val MessageString: String,
    val ShowShift: String,
    val UserId: String,
    val UserName: String
)