package com.example.chatappfirebase.ui.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

open class BaseViewModel : ViewModel() {

    var parentJob: Job? = null
        protected set

    var onNavigateTopage = MutableLiveData<Int>()
        protected set

    var isLoadingMore = MutableLiveData<Boolean>()
        protected set

    var isLoading = MutableLiveData<Boolean>()
        protected set

    protected fun registerJobFinish() {
        parentJob?.invokeOnCompletion {
        }
    }

    protected fun showLoading(isShow: Boolean) {
        isLoading.postValue(isShow)
    }

    protected fun showLoadingMore(isShow: Boolean) {
        isLoadingMore.postValue(isShow)
    }

}