package com.uni.uniadmin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uni.uniadmin.data.FirebaseRealtimeRepo
import com.uni.uniadmin.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseRealtimeModelView @Inject constructor(
   private val repository: FirebaseRealtimeRepo
): ViewModel() {




    private val _getAttendanceCode= MutableStateFlow<Resource<Boolean>?>(null)
    val getAttendanceCode=_getAttendanceCode.asStateFlow()

    fun getAttendanceCode (embeddedId:String,scannedCode:Int)= viewModelScope.launch {
        _getAttendanceCode.value= Resource.Loading
        repository.getAttendWithCode(embeddedId,scannedCode){
            _getAttendanceCode.value=it
        }
    }
}


