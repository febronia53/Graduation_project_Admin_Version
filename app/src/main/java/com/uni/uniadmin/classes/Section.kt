package com.uni.uniadmin.classes



data class Section (
    var sectionId : String="",
    val courseCode:String="",
    val courseName:String="",
    val lapID:String="",
    val assistantName:String="",
    val section : String="",
    val day:String="",
    val time:String="",
    val endTime:String="",
    val isRunning:Boolean=false

)