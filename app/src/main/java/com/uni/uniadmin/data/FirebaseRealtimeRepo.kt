package com.uni.uniadmin.data


interface FirebaseRealtimeRepo {
    suspend fun getAttendWithCode(embeddedId:String,scannedCode:Int,result :(Resource<Boolean>) ->Unit)
}