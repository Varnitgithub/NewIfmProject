package com.example.ifmapp.shared_preference.shared_preference_models

data class CurrentUserShiftsDetails(
    var shiftDetails: String,
    val site: String,
    var empId: String,
    var asmtId:String
)
