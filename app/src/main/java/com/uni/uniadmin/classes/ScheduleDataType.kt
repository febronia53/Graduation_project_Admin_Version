package com.uni.uniadmin.classes

data class ScheduleDataType(

    val eventId:String="",
    val courseName:String="",
    val courseID:String="",
    val hallID:String="",
    val professorName:String="",
    val day:String="",
    val time:String="",
    val endTime:String="",
    val type:Int=0,
    val isRunning:Boolean=false
)
