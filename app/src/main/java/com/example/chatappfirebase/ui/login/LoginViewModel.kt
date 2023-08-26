package com.example.chatappfirebase.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatappfirebase.data.repository.AuthRepository
import com.example.chatappfirebase.ui.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepo: AuthRepository) : BaseViewModel() {
    private val _loginpResult = MutableLiveData<Any>()
    val loginResult : LiveData<Any> get() = _loginpResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginpResult.value = authRepo.login(email, password)
        }
    }
}