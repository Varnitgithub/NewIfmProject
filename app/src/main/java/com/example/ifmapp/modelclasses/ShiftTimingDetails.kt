package com.example.ifmapp.modelclasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("ShiftTiming")
data class ShiftTimingDetails(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var shiftCode: String? = "",
    var shiftTiming: String? = ""
)
