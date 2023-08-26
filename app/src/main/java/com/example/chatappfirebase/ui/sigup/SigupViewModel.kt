package com.example.chatappfirebase.ui.sigup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatappfirebase.data.repository.AuthRepository
import com.example.chatappfirebase.ui.common.BaseViewModel
import com.example.chatappfirebase.utils.RepoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SigupViewModel @Inject constructor(private val authRepo: AuthRepository) : BaseViewModel() {


    private val _signupResult = MutableLiveData<Any>()
    val sigupResult : LiveData<Any> get() = _signupResult
    fun sigup(email: String, password: String) {
        _signupResult.value = RepoResult.Loading
        viewModelScope.launch {
            _signupResult.value = authRepo.sigup(email, password)
        }
    }


}