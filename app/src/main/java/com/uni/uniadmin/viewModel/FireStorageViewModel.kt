package com.uni.uniadmin.viewModel
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uni.uniadmin.data.FireStorageRepo
import com.uni.uniadmin.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FireStorageViewModel @Inject constructor(
    private val repository: FireStorageRepo
): ViewModel() {

    private val _getUri= MutableStateFlow<Resource<Uri>?>(null)
    val getUri=_getUri.asStateFlow()

    private val _addUri= MutableStateFlow<Resource<String>?>(null)
    val addUri=_addUri.asStateFlow()

    fun getUri(userId:String)= viewModelScope.launch{
        _getUri.value= Resource.Loading
        repository.downloadUri(userId) {
            _getUri.value=it
        }
    }
    private val _addPostUri= MutableStateFlow<Resource<String>?>(null)
    val addPostUri=_addPostUri.asStateFlow()
    fun addPostUri(postID:String,uri:Uri)= viewModelScope.launch{
        _addPostUri.value= Resource.Loading
        repository.uploadPostImage(uri,postID) {
            _addPostUri.value=it
        }
    }
    private val _deletePostImage= MutableStateFlow<Resource<String>?>(null)
    val deletePostImage=_deletePostImage.asStateFlow()
    fun deletePostImage(postID:String)= viewModelScope.launch{
        _deletePostImage.value= Resource.Loading
        repository.deletePostImage(postID) {
            _deletePostImage.value=it
        }
    }

    fun addUri(userId:String,uri:Uri)= viewModelScope.launch{
        _addUri.value= Resource.Loading
        repository.uploadImage(uri,userId) {
            _addUri.value=it
        }
    }

}