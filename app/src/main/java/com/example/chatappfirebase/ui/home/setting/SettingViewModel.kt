package com.example.chatappfirebase.ui.home.setting

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatappfirebase.data.repository.DatabaseRepository
import com.example.chatappfirebase.utils.RepoResult
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.data.repository.StorageRepository
import com.example.chatappfirebase.ui.common.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: DatabaseRepository,
    private val storage: StorageRepository
) : BaseViewModel() {

    private val _user = MutableLiveData<RepoResult<User>>()
    val user : LiveData<RepoResult<User>> get() = _user
    fun fetchInfoUser() {
        viewModelScope.launch {
            _user.value = database.getInfoUser(auth.currentUser!!.uid)
        }
    }

    private val _updateProfileUser = MutableLiveData<RepoResult<Boolean>>()
    val updateProfileUserResult : LiveData<RepoResult<Boolean>> get() = _updateProfileUser

    fun updateProfileUser(user: User) {
        viewModelScope.launch {
            _updateProfileUser.value = database.updateProfileUser(user)
        }
    }

    private val _updateAvatar = MutableLiveData<RepoResult<String>>()
    val updateAvatarResult : LiveData<RepoResult<String>> get() = _updateAvatar
    fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            _updateAvatar.value = storage.uploadImageToFirebase(uri)
        }
    }


}