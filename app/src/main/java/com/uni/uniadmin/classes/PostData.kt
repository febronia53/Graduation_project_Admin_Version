package com.uni.uniadmin.classes

import java.util.*


data class PostData (
    val description : String="",
    val authorName : String="",
    var myPost: Boolean=false,
    var postID:String="",
    var courseID:String="",
    val time: Date = Date(),
    val audience:String="",
    var type:Int=0
)