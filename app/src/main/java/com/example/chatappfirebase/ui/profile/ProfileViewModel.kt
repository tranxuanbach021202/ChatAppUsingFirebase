package com.example.chatappfirebase.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatappfirebase.data.repository.DatabaseRepository
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.data.repository.StorageRepository
import com.example.chatappfirebase.ui.common.BaseViewModel
import com.example.chatappfirebase.utils.RepoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val database: DatabaseRepository,
    private val storage: StorageRepository) : BaseViewModel() {

    private val _createProfileResult = MutableLiveData<Any>()
    val createProfileResult : LiveData<Any> get() = _createProfileResult
    fun createProfileUser(user: User) {
        viewModelScope.launch {
            _createProfileResult.value = database.createProfileUser(user)
        }
    }

    private val _uploadImageAvatar = MutableLiveData<RepoResult<String>>()
    val uploadImageAvatarResult : LiveData<RepoResult<String>> get() = _uploadImageAvatar
    fun uploadImageAvatar(uri: Uri) {
        viewModelScope.launch {
            _uploadImageAvatar.value = storage.uploadImageToFirebase(uri)
        }
    }
}