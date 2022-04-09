package com.wesleyfranks.todo24.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class GetTimestamp {
    var milTime:String = "H:mm:ss YYYY-MM-dd"
    // MIL EX. 09 1630Z JUL 11
    var time:String = "h:mm:ssa MM/dd/YYYY"

    fun getCurrentTime():Long {
        return System.currentTimeMillis()
    }

    @SuppressLint("SimpleDateFormat")
    fun formateNonMilDateTime(long: Long):String {
        val formatter = SimpleDateFormat(time)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = long
        calendar.timeZone = TimeZone.getDefault()
        return formatter.format(calendar.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun formateMilDateTime(long: Long):String{
        val formatter = SimpleDateFormat(milTime)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = long
        calendar.timeZone = TimeZone.getDefault()
        return formatter.format(calendar.time)
    }

}