package com.uni.uniadmin.data


import com.google.firebase.auth.FirebaseUser
import com.uni.uniteaching.classes.user.UserAdmin

interface AuthRepository {
    val user:FirebaseUser?
    suspend fun logIn(email:String, password:String, result:(Resource<String>) ->Unit)

    suspend fun updateUserInfo(userAdmin: UserAdmin, result:(Resource<String>) ->Unit)
    suspend fun register(email:String, password:String, userAdmin: UserAdmin, result:(Resource<String>) -> Unit)
    suspend fun logOut(result:()->Unit)
    fun storeSession(id :String, user : UserAdmin, result :(UserAdmin?)-> Unit)
    suspend fun getUser(id :String, result:(Resource<UserAdmin?>) -> Unit)
    fun getSession(result :(UserAdmin?)-> Unit)
    fun setSession(user: UserAdmin)


}