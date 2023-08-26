package com.example.chatappfirebase.data.repository

import android.util.Log
import com.example.chatappfirebase.data.datasource.FirebaseDatabaseSource
import com.example.chatappfirebase.data.models.ChatRoom
import com.example.chatappfirebase.data.models.Message
import com.example.chatappfirebase.data.models.Relationship
import com.example.chatappfirebase.data.models.RelationshipStatus
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.data.models.UserRelationship
import com.example.chatappfirebase.data.models.UserStatus
import com.example.chatappfirebase.di.IoDispatcher
import com.example.chatappfirebase.utils.RepoResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    val databseSource: FirebaseDatabaseSource,
    @IoDispatcher val dispatcher: CoroutineDispatcher
) {


    suspend fun createProfileUser(user : User) = withContext(dispatcher) {
        when(val result = databseSource.createProfileUser(user)) {
            is RepoResult.Success -> {
                result.data
            }

            is RepoResult.Error -> {
               result.exception
            }

            is RepoResult.Loading -> {}
        }

    }

    // a
    suspend fun createRelationship(uid: String, userRelationship: UserRelationship) = withContext(dispatcher) {
        databseSource.createRelationship(uid, userRelationship)
    }


    suspend fun getInfoUser(userId: String) = withContext(dispatcher) {
        when(val result = databseSource.getInfoUser(userId)) {
            is RepoResult.Success -> {
                RepoResult.Success(result.data)
            }
            is RepoResult.Error -> {
                RepoResult.Error(result.exception)
            }
            is RepoResult.Loading -> {
                RepoResult.Loading
            }
        }
    }

    suspend fun updateProfileUser(user: User) = withContext(dispatcher) {
        when(val result = databseSource.updateProfileUser(user)) {
            is RepoResult.Success -> {
                RepoResult.Success(result.data)
            }
            is RepoResult.Error -> {
                RepoResult.Error(result.exception)
            }
            is RepoResult.Loading -> {
                RepoResult.Loading
            }
        }
    }


    suspend fun updateRelationshipUser(relationship: Relationship) = withContext(dispatcher) {
        when(val result = databseSource.updateRelationshipUser(relationship)) {
            is RepoResult.Success -> {
                RepoResult.Success(result.data)
            }
            is RepoResult.Error -> {
                RepoResult.Error(result.exception)
            }
            is RepoResult.Loading -> {
                RepoResult.Loading
            }
        }
    }


    suspend fun fetchListFriendOnline(uid: String) = withContext(dispatcher){
        val listRelationshipFriend =
            databseSource.fetchListRelationship(uid, RelationshipStatus.FRIEND)

        Log.d("DataFriend", listRelationshipFriend.toString())
        val listUser = databseSource.getUserList(uid)

        Log.d("DataFriendU", listUser.toString())

        when {
            listRelationshipFriend is RepoResult.Success && listUser is RepoResult.Success -> {
                val listRelationshipData = listRelationshipFriend.data
                Log.d("DataFriend", listRelationshipData.toString())
                val listUserData = listUser.data

                val listFriendOnline = mutableListOf<User>()
                for (user in listUserData) {
                    val relationship = listRelationshipData.find { it.receiverId == user.uid }
                    if (relationship != null) {
                        if (user.status == UserStatus.ONLINE.toString()) {
                            listFriendOnline.add(
                                User(
                                    user.uid!!,
                                    user.name!!,
                                    user.imgProfile!!,
                                    user.gender!!,
                                    user.phoneNumber!!,
                                    user.address!!,
                                    user.status
                                )
                            )
                        }
                    }
                }
                RepoResult.Success(listFriendOnline)
            }

            listRelationshipFriend is RepoResult.Error -> {
                RepoResult.Error(listRelationshipFriend.exception)
            }

            listUser is RepoResult.Error -> {
                RepoResult.Error(listUser.exception)
            }

            else -> {
                RepoResult.Loading
            }
        }
    }

    suspend fun sendMessage(message: Message, senderRoom: String, receiverRoom: String) = withContext(dispatcher){
        Log.d("Message", message.toString())
        Log.d("SenderRoom", senderRoom)
        Log.d("ReceiverRoom", receiverRoom)
        when(val result = databseSource.sendMessage(message, senderRoom, receiverRoom)) {
            is RepoResult.Success -> {
                RepoResult.Success(result.data)
            }
            is RepoResult.Error -> {
                RepoResult.Error(result.exception)
            }
            is RepoResult.Loading -> {
                RepoResult.Loading
            }
        }
    }

    suspend fun checkChatRoom(roomId: String) = withContext(dispatcher){
        when (val result = databseSource.checkChatRoom(roomId)) {
            is RepoResult.Success -> {
                RepoResult.Success(result.data)
            }
            is RepoResult.Error -> {
                RepoResult.Error(result.exception)
            }
            is RepoResult.Loading -> {
                RepoResult.Loading
            }
        }
    }

    suspend fun createChatRoom(room: ChatRoom, uid: String) = withContext(dispatcher) {
        when(val result =  databseSource.createRoom(room, uid)) {
            is RepoResult.Success -> {
                RepoResult.Success(result.data)
            }
            is RepoResult.Error -> {
                RepoResult.Error(result.exception)
            }
            is RepoResult.Loading -> {
                RepoResult.Loading
            }
        }
    }


    suspend fun getListChatRoom(uid: String) = withContext(dispatcher) {
        when(val result = databseSource.getListChatRoom(uid)) {
            is RepoResult.Success -> {
                RepoResult.Success(result.data)
            }
            is RepoResult.Error -> {
                RepoResult.Error(result.exception)
            }
            is RepoResult.Loading -> {
                RepoResult.Loading
            }
        }
    }

    fun observerMessageChatRoom(roomId: String) : StateFlow<List<Message>> {
        val result =  databseSource.observeMessageChatRoomChanges(roomId)
        return result
    }

    fun observerUserRelationship(uid: String, status: RelationshipStatus) : StateFlow<RepoResult<List<UserRelationship>>> {
        val result = databseSource.observeUserRelationship(uid, status)
        return result
    }

    fun observerListUser(uid: String) : StateFlow<RepoResult<List<User>>> {
        val result = databseSource.observeListUser(uid)
        return result
    }

    suspend fun updateRelationship(uid: String, userRelationship: UserRelationship) = withContext(dispatcher) {
        databseSource.updateRelationship(uid, userRelationship)
    }

    fun observerUserRelationshipByStatus(uid: String, status: RelationshipStatus) : StateFlow<RepoResult<List<UserRelationship>>> {
        val result = databseSource.observeUserRelatinshipByStatus(uid, status)
        return result
    }

    suspend fun updateLastMessageChatRoom(userId: String, chatRoomId: String, message: Message) = withContext(dispatcher) {
        databseSource.updateLastMessageChatRoom(userId, chatRoomId, message)
    }

    suspend fun updateStatusUser(userId: String, status: String) = withContext(dispatcher){
        databseSource.updateStatusUser(userId, status)
    }


}