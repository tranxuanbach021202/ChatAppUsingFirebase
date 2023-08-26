package com.example.chatappfirebase.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.data.models.UserStatus
import com.example.chatappfirebase.data.repository.DatabaseRepository
import com.example.chatappfirebase.ui.common.BaseViewModel
import com.example.chatappfirebase.utils.RepoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: DatabaseRepository
) : BaseViewModel() {

    val userCurrent = MutableLiveData<User>()
    private val _infoUserCurrent = MutableLiveData<RepoResult<User>>()
    val infoUserCurrent: LiveData<RepoResult<User>> get() = _infoUserCurrent

    fun fetchInfoUserCurrent(uid: String) {
        viewModelScope.launch {
                _infoUserCurrent.value = database.getInfoUser(uid)
        }
    }


    fun updateStatusUser(uid: String, status: String) {
        viewModelScope.launch {
            database.updateStatusUser(uid, status)
        }
    }
}