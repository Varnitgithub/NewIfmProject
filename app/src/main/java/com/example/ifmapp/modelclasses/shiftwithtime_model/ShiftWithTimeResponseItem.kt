package com.example.ifmapp.modelclasses.shiftwithtime_model

data class ShiftWithTimeResponseItem(
    val InTime: String,
    val OutTime: String,
    val ShiftCode: String,
    val ShiftDetails: String
)