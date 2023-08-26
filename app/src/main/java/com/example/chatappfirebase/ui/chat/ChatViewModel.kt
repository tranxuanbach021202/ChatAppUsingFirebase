package com.example.chatappfirebase.ui.chat

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chatappfirebase.data.models.ChatRoom
import com.example.chatappfirebase.data.models.Message
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.data.repository.DatabaseRepository
import com.example.chatappfirebase.data.repository.StorageRepository
import com.example.chatappfirebase.ui.common.BaseViewModel
import com.example.chatappfirebase.utils.RepoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val database : DatabaseRepository,
    private val storage: StorageRepository
) : BaseViewModel() {

    private val _sendMessage = MutableLiveData<RepoResult<Boolean>>()
    val sendMessageResult: MutableLiveData<RepoResult<Boolean>>get() = _sendMessage
    fun sendMessage(message: Message, senderRoom: String, receiverRoom: String) {
        viewModelScope.launch {
            _sendMessage.value = database.sendMessage(message, senderRoom, receiverRoom)
        }
    }


    private val _fetchInfoUser = MutableLiveData<RepoResult<User>>()
    val fetchInfoUserResult: MutableLiveData<RepoResult<User>>get() = _fetchInfoUser
    fun fetchInfoUser(userId: String) {
        viewModelScope.launch {
            _fetchInfoUser.value = database.getInfoUser(userId)
        }
    }

    private val _checkChatRoom = MutableLiveData<RepoResult<Boolean>>()
    val checkChatRoomResult: MutableLiveData<RepoResult<Boolean>>get() = _checkChatRoom
    fun checkChatRoom(roomId: String) {
        viewModelScope.launch {
            _checkChatRoom.value = database.checkChatRoom(roomId)
        }
    }

    private val _createRoom = MutableLiveData<RepoResult<Boolean>>()
    val createRoomResult: MutableLiveData<RepoResult<Boolean>>get() = _createRoom
    fun createChatRoom(chatRoom: ChatRoom, uid: String) {
        viewModelScope.launch {
            _createRoom.value = database.createChatRoom(chatRoom, uid)
        }
    }


    private val _messageListLiveData = MutableLiveData<List<Message>>()
    val messageListLiveData: LiveData<List<Message>>get() = _messageListLiveData
    fun observeMessageChatRoomChanges(roomId: String){
        val messageListStateFlow = database.observerMessageChatRoom(roomId)
        viewModelScope.launch {
            messageListStateFlow.collect{
                Log.d("FlowMessag", it.toString())
                _messageListLiveData.value = it
            }
        }
    }

    fun updateLastMessage(userId: String, roomId: String, message: Message) {
        viewModelScope.launch {
            database.updateLastMessageChatRoom(userId, roomId, message)
        }
    }

    private val _uploadImage = MutableLiveData<RepoResult<String>>()
    val uploadImageAvatarResult : LiveData<RepoResult<String>> get() = _uploadImage
    fun uploadImageAvatar(uri: Uri) {
        viewModelScope.launch {
            _uploadImage.value = storage.uploadImageToFirebase(uri)
        }
    }


}