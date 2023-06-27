package com.uni.uniadmin.classes

import java.util.*


data class MyComments(
    var commentID: String = "",
    val description: String = "",
    val authorName: String = "",
    val author_id: String = "",
    var myComment: Boolean = false,
    val time: Date = Date()
)