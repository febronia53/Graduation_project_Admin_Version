package com.uni.uniadmin.data

import android.content.SharedPreferences

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.uni.uniadmin.data.di.FireStoreTable
import com.uni.uniteaching.classes.user.UserAdmin
import com.uni.uniadmin.data.di.SharedPreferencesTable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepositoryImpl@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database:FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson:Gson
) : AuthRepository {

    override suspend fun updateUserInfo(userAdmin: UserAdmin, result: (Resource<String>) ->Unit ) {
            val document=database.collection(FireStoreTable.user)
                .document(userAdmin.userId)
            document.set(userAdmin)
                .addOnSuccessListener {
                    result.invoke(
                        Resource.Success("user date updated successfully")
                    )
                }
                .addOnFailureListener{
                    result.invoke(
                        Resource.Failure(
                            it.localizedMessage
                        )
                    )
                }

    }

    override val user:FirebaseUser?
    get() =firebaseAuth.currentUser

    override suspend fun logIn(
        email: String,
        password: String,
        result: (Resource<String>) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                result.invoke(
                    Resource.Success("user date updated successfully")
                )
            }else{
                Resource.Failure(
                    it.exception.toString()
                )
            }}
                .addOnFailureListener {
                    result.invoke(
                        Resource.Failure(
                            it.localizedMessage
                        )
                    )
                }


    }

    override suspend fun register(
        email: String,
        password: String,
        userAdmin: UserAdmin,
        result: (Resource<String>) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    GlobalScope.launch {
                        val userId=it.result.user?.uid?:""
                        userAdmin.userId=userId
                        updateUserInfo(userAdmin){ state->
                            when(state){
                                Resource.Loading -> result.invoke(Resource.Loading)
                                is Resource.Success -> {
                                    storeSession(userId,userAdmin) { user->
                                        if (user == null) {
                                            result.invoke(Resource.Failure("user created successfully but session did not stored"))
                                        } else {



                                               result.invoke(
                                                    Resource.Success(
                                                        "user created successfully but you need to check you permission with admin"
                                                    )
                                                )

                                                }
                                    }
                                }
                                is Resource.Failure ->{result.invoke(Resource.Failure(state.exception))}


                            }
                        }

                    }
                }else{
                    result.invoke(
                        Resource.Failure(
                            it.exception.toString()
                        )
                    )
                }
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    override suspend fun logOut(result: () -> Unit) {
        firebaseAuth.signOut()
        result.invoke()
        appPreferences.edit().putString(SharedPreferencesTable.user_session,null).apply()

    }


    override fun storeSession(id :String, user : UserAdmin, result :(UserAdmin?)-> Unit){
       val document =database.collection(FireStoreTable.user)
           .document(user.userId)
        document
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val userAdmin = it.result.toObject(UserAdmin::class.java)
                    if (userAdmin != null) {
                        setSession(userAdmin)
                    }
                    result.invoke(userAdmin)
                }else{
                    result.invoke(null)
                }
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun getSession(result: (UserAdmin?) -> Unit) {
        val user = appPreferences.getString(SharedPreferencesTable.user_session,null)
        if (user==null){
            result.invoke(null)
        }else{
            val userAdmin = gson.fromJson(user, UserAdmin::class.java)
            result.invoke(userAdmin)
        }
    }
    override  suspend fun getUser(id :String, result:(Resource<UserAdmin?>) -> Unit) {

        val docRef =  database.collection(FireStoreTable.user)
            .document(id)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }
            result.invoke(Resource.Success(snapshot?.toObject(UserAdmin::class.java)))
        }
    }
    override fun setSession(user: UserAdmin) {
        appPreferences.edit().putString(SharedPreferencesTable.user_session,gson.toJson(user)).apply()
    }



}