package com.uni.uniadmin.data

import android.net.Uri

interface FireStorageRepo {
    suspend  fun downloadUri(userId:String ,result: (Resource<Uri>) -> Unit)
    suspend fun uploadImage( imageUri: Uri, userId:String, result: (Resource<String>) -> Unit)
    suspend  fun downloadPostUri(postId:String ,result: (Resource<Uri>) -> Unit)
    suspend fun uploadPostImage( imageUri: Uri, postId: String, result: (Resource<String>) -> Unit)
}