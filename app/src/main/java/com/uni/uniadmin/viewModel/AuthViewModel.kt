package com.uni.uniadmin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.uni.uniteaching.classes.user.UserAdmin
import com.uni.uniadmin.data.AuthRepository
import com.uni.uniadmin.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository):ViewModel() {

    private val _register = MutableStateFlow<Resource<String>?>(null)
    val register=_register.asStateFlow()

    private val _logIn = MutableStateFlow<Resource<String>?>(null)
    val logIn=_logIn.asStateFlow()

fun logIn(email:String, password:String)=viewModelScope.launch {
    _logIn.value= Resource.Loading
    repository.logIn(email,password){
        _logIn.value=it
    }
}
    private val _userAdmin = MutableStateFlow<Resource<UserAdmin?>?>(null)
    val userStudent=_userAdmin.asStateFlow()

    val currentUser:FirebaseUser?
        get()=repository.user


    fun register(email:String, password:String, user: UserAdmin) = viewModelScope.launch {
      _register.value= Resource.Loading
      repository.register(email,password,user){
              _register.value=it
          }

    }
    fun getUserStudent(id :String)= viewModelScope.launch{
        _userAdmin.value= Resource.Loading
        repository.getUser(id){
            _userAdmin.value=it
        }
    }

    fun setSession(user: UserAdmin){
        repository.setSession(user)
    }
    fun logOut(result:()->Unit)= viewModelScope.launch {
        repository.logOut (result)
    }
fun getSessionStudent(result: (UserAdmin?) -> Unit){
    repository.getSession(result)
}
}