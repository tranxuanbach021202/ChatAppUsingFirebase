package com.example.chatappfirebase.ui.home.friend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.Transformation
import com.example.chatappfirebase.data.models.Relationship
import com.example.chatappfirebase.data.models.RelationshipStatus
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.data.models.UserRelationship
import com.example.chatappfirebase.data.repository.DatabaseRepository
import com.example.chatappfirebase.ui.common.BaseViewModel
import com.example.chatappfirebase.utils.RepoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val database : DatabaseRepository) : BaseViewModel() {



    val TAG = "FriendViewModel"

    private val _userCurrent = MutableLiveData<RepoResult<User>>()
    val userCurrent : LiveData<RepoResult<User>> get() = _userCurrent
    fun fetchInfoUserCurrent(uid: String) {
        viewModelScope.launch {
            _userCurrent.value = database.getInfoUser(uid)
        }
    }

    private val _userRelationship = MutableLiveData<List<UserRelationship>>()
    val userRelationship : LiveData<List<UserRelationship>> get() = _userRelationship
    fun observeUserRelationship(uid: String, status: RelationshipStatus) {
        viewModelScope.launch {
            database.observerUserRelationship(uid, status).collect{
                when(it) {
                    is RepoResult.Success -> {
                        Log.d(TAG, it.toString())
                        _userRelationship.value = it.data
                    }
                    is RepoResult.Error -> {
                       Log.e(TAG, "observer user relationship failed")
                    }
                    is RepoResult.Loading -> {
                        Log.d(TAG, "observer user relationship loading")
                    }
                }
            }
        }
    }

    private val _listUser = MutableLiveData<List<User>>()
    val listUser : LiveData<List<User>>get() = _listUser
    fun observeListUser(uid: String){
        viewModelScope.launch {
            database.observerListUser(uid).collect{
                when(it) {
                    is RepoResult.Success -> {
                        Log.d(TAG, it.toString())
                        _listUser.value = it.data
                    }
                    is RepoResult.Error -> {
                       Log.e(TAG, "observer list user failed")
                    }
                    is RepoResult.Loading -> {
                        Log.d(TAG, "observer list user loading")
                    }
                }
            }
        }
    }


    val userRelationshipCheck: MediatorLiveData<List<UserRelationship>> = MediatorLiveData()
    fun mediatorLiveData() {
        userRelationshipCheck.addSource(listUser) { listUser ->
            val userRelationshipMap = userRelationship.value?.associateBy { it.uidFriend }
            val filteredUserRelationshipList = listUser.mapNotNull {
                val relationship = userRelationshipMap?.get(it.uid)
                if (relationship != null) {
                    Log.d("FriendViewModel", relationship.toString())
                    relationship
                } else {
                    Log.d("FriendViewModell", relationship.toString())
                    UserRelationship(
                        it.uid!!, it.name!!, it.imgProfile!!, RelationshipStatus.STRANGERS.toString()
                    )
                }
            }
            userRelationshipCheck.value = filteredUserRelationshipList
        }
    }

    fun createRelationship(uid: String, userRelationship: UserRelationship) {
        viewModelScope.launch {
            database.createRelationship(uid, userRelationship)
        }
    }


    fun updateUserRelationship(uid: String, userRelationship: UserRelationship) {
        viewModelScope.launch {
            database.updateRelationship(uid, userRelationship)
        }
    }

    private val _userRelationshipByStatus = MutableLiveData<List<UserRelationship>>()
    val userRelationshipByStatus : LiveData<List<UserRelationship>> get() = _userRelationship
    fun observerUserRelationshipByStatus(uid: String, status: RelationshipStatus) {
        viewModelScope.launch{
            database.observerUserRelationshipByStatus(uid, status).collect{
                when(it) {
                    is RepoResult.Success -> {
                        Log.d(TAG, it.toString())
                        _userRelationshipByStatus.value = it.data
                    }
                    is RepoResult.Error -> {
                       Log.e(TAG, "observer user relationship failed")
                    }
                    is RepoResult.Loading -> {
                        Log.d(TAG, "observer user relationship loading")
                    }
                }
            }
        }
    }

    private val _userRelationshipByFriend = MutableLiveData<List<UserRelationship>>()
    val userRelationshipByFriend : LiveData<List<UserRelationship>> get() = _userRelationshipByFriend
    fun observerUserRelationshipByFriend(uid: String) {
        viewModelScope.launch{
            database.observerUserRelationshipByStatus(uid, RelationshipStatus.FRIEND).collect{
                when(it) {
                    is RepoResult.Success -> {
                        Log.d(TAG, it.toString())
                        _userRelationshipByFriend.value = it.data
                    }
                    is RepoResult.Error -> {
                       Log.e(TAG, "observer user relationship failed")
                    }
                    is RepoResult.Loading -> {
                        Log.d(TAG, "observer user relationship loading")
                    }
                }
            }
        }
    }

    private val _userRelationshipByFriendRequest = MutableLiveData<List<UserRelationship>>()
    val userRelationshipByFriendRequest : LiveData<List<UserRelationship>> get() = _userRelationshipByFriendRequest
    fun observerUserRelationshipByFriendRequest(uid: String) {
        viewModelScope.launch {
            database.observerUserRelationshipByStatus(uid, RelationshipStatus.FRIEND_REQUEST).collect{
                when(it) {
                    is RepoResult.Success -> {
                        Log.d(TAG, it.toString())
                        _userRelationshipByFriendRequest.value = it.data
                    }
                    is RepoResult.Error -> {
                       Log.e(TAG, "observer user relationship friend request failed")
                    }
                    is RepoResult.Loading -> {
                        Log.d(TAG, "observer user relationship friend request loading")
                    }
                }
            }
        }
    }


    init {
        mediatorLiveData()
    }


}









