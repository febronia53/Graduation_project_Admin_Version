package com.uni.uniadmin.classes

import java.util.*

data class Comment (
    var commentID:String="",
    var userID:String="",
    val description : String="",
    val authorName : String="",
    val time: Date = Date()
//TODO set user type for the comment
)