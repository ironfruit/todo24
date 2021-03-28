package com.wesleyfranks.todo24.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class GetTimestamp {
    var milTime:String = "DDHHMM(Z)MONYY"
    var time:String = "h:mma MM/dd/YYYY"

    fun getTimeOnDevice():String{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(time)
        return current.format(formatter)
    }

    fun getMilTimeOnDevice():String{
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(milTime)
        return current.format(formatter)
    }

}