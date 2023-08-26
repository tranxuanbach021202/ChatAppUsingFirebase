package com.example.chatappfirebase.ui.home.message

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatappfirebase.data.models.ChatRoom
import com.example.chatappfirebase.data.repository.DatabaseRepository
import com.example.chatappfirebase.utils.RepoResult
import com.example.chatappfirebase.data.models.Relationship
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.data.models.UserRelationship
import com.example.chatappfirebase.ui.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeMessageViewModel @Inject constructor(
    private val database: DatabaseRepository
) : BaseViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading


    private val _listFriendOnline = MutableLiveData<RepoResult<List<User>>>()
    val listFriendOnlineResult : LiveData<RepoResult<List<User>>> get() = _listFriendOnline
    fun fetchListFriendOnline(uid: String) {
        viewModelScope.launch {
            _listFriendOnline.value = database.fetchListFriendOnline(uid)
        }
    }


    fun fetchData(uid: String) {

        _loading.value = true
        val job = viewModelScope.launch {
            val listFriendOnlineDeferred = async { fetchListFriendOnline(uid) }
            val listChatRoomDeferred = async { fetchListChatRoom(uid) }
            val infoUserCurrentDeferred = async { fetchInfoUserCurrent(uid)}
            val result = awaitAll(listFriendOnlineDeferred, listChatRoomDeferred, infoUserCurrentDeferred)
            delay(1000)
           _loading.value = false
        }
    }

    private val _listChatRoom = MutableLiveData<RepoResult<List<ChatRoom>>>()
    val listChatRoomResult : LiveData<RepoResult<List<ChatRoom>>> get() = _listChatRoom
    fun fetchListChatRoom(uid: String) {
        _listChatRoom.value = RepoResult.Loading
        viewModelScope.launch {
            _listChatRoom.value = database.getListChatRoom(uid)
        }
    }


    private val _userCurrent = MutableLiveData<RepoResult<User>>()
    val userCurrent : LiveData<RepoResult<User>> get() = _userCurrent
    fun fetchInfoUserCurrent(uid: String) {
        viewModelScope.launch {
            _userCurrent.value = database.getInfoUser(uid)
        }
    }













}