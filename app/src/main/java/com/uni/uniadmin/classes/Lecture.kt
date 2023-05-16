package com.uni.uniadmin.classes




data class Lecture (
    var lectureId:String="",
    val courseCode:String="",
    val courseName:String="",
    val hallID:String="",
    val professorName:String="",
    val day:String="",
    val time:String="",
    val endTime:String="",
    val isRunning:Boolean=false

)