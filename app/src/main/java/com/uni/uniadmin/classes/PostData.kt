package com.uni.uniadmin.classes

import android.net.Uri
import java.util.*


data class PostData (
    val description : String="",
    val authorName : String="",
    var myPost: Boolean=false,
    var postID:String="",
    var courseID:String="",
    val time: Date = Date(),
    val audience:String="",
    var postUri: Uri = Uri.EMPTY,
    var type:Int=0
)