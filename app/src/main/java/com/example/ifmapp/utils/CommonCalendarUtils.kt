package com.example.ifmapp.utils

import java.text.SimpleDateFormat
import java.util.*

class CommonCalendarUtils(private val callback: CalendarUpdateCallback) {

    interface CalendarUpdateCallback {
        fun updatesCalendar(calendar: Calendar)
    }

    fun updatePreviousMonth(calendar: Calendar) {
        calendar.add(Calendar.MONTH, -1)
        callback.updatesCalendar(calendar)
    }

    fun updateNextMonth(calendar: Calendar) {
        calendar.add(Calendar.MONTH, 1)
        callback.updatesCalendar(calendar)
    }
}
