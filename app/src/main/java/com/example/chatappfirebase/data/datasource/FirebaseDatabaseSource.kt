package com.example.chatappfirebase.data.datasource

import android.util.Log
import com.example.chatappfirebase.data.models.ChatRoom
import com.example.chatappfirebase.data.models.Message
import com.example.chatappfirebase.utils.RepoResult
import com.example.chatappfirebase.data.models.Relationship
import com.example.chatappfirebase.data.models.RelationshipStatus
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.data.models.UserRelationship
import com.example.chatappfirebase.data.models.UserStatus
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseDatabaseSource(private val database: DatabaseReference)  {


    suspend fun createProfileUser(user: User): RepoResult<Boolean> {
       return try {
           database.child("users")
                    .child(user.uid!!)
                    .setValue(user)
                    .await()
           RepoResult.Success(true)
        } catch (ex: Exception) {
            RepoResult.Error(ex)
        }
    }


    suspend fun getUserList(uidCurrent : String) : RepoResult<List<User>> {
        return try {
                val deferred = CompletableDeferred<RepoResult<List<User>>>()

                database.child("users")
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val users = mutableListOf<User>()
                            for (it in snapshot.children) {
                                val user = it.getValue(User::class.java)
                                user?.let {
                                    if (uidCurrent != user.uid) {
                                        users.add(user)
                                    }
                                }
                            }
                            deferred.complete(RepoResult.Success(users))

                        }

                        override fun onCancelled(error: DatabaseError) {
                            deferred.complete(RepoResult.Error(error.toException()))
                        }

                    })
            deferred.await()
        } catch (ex: Exception) {
            RepoResult.Error(ex)
        }
    }

    fun observeListUser(uid: String) : StateFlow<RepoResult<List<User>>> {
        val listUserStateFlow = MutableStateFlow<RepoResult<List<User>>>(RepoResult.Loading)
        database.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = mutableListOf<User>()
                    for (it in snapshot.children) {
                        val user = it.getValue(User::class.java)
                        user?.let {
                            if (uid!= user.uid) {
                                users.add(user)
                            }
                        }
                    }

                    listUserStateFlow.value = RepoResult.Success(users)
                }
                override fun onCancelled(error: DatabaseError) {
                    listUserStateFlow.value = RepoResult.Error(error.toException())
                }
            })
        return listUserStateFlow
    }

    suspend fun createRelationshipUser(relationshipSender: Relationship, relationshipReceiver: Relationship) : RepoResult<Boolean> {
        return try {
            database.child("friend_request")
                .child(relationshipSender.senderId!!)
                .child(relationshipSender.receiverId!!)
                .setValue(relationshipSender)
                .await()


            database.child("friend_request")
                .child(relationshipReceiver.senderId!!)
                .child(relationshipReceiver.receiverId!!)
                .setValue(relationshipReceiver)
                .await()

          RepoResult.Success(true)
        } catch (ex: Exception) {
            RepoResult.Error(ex)
        }
    }


    suspend fun createRelationship(uid: String, userRelationship: UserRelationship) {
        try {
            val key = database.push().key
            // create relationship of sender
            database.child("friend_request")
                .child(uid)
                .child(userRelationship.uidFriend)
                .setValue(userRelationship)
                .await()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun updateRelationship(uid: String, userRelationship: UserRelationship) {
        try {
            val childUpdate = hashMapOf<String, Any>(
                "relationship" to userRelationship.relationship
            )
            database.child("friend_request")
               .child(uid)
               .child(userRelationship.uidFriend)
               .updateChildren(childUpdate)
               .await()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }



    suspend fun getInfoUser(userId: String) : RepoResult<User> {
        return try {
            val deferred = CompletableDeferred<RepoResult<User>>()
            database.child("users")
                .child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user : User? = snapshot.getValue(User::class.java)
                        Log.d("Data", user.toString())
                        deferred.complete(RepoResult.Success(user!!))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        deferred.complete(RepoResult.Error(error.toException()))
                    }

                })
            deferred.await()
        } catch (ex: Exception) {
            RepoResult.Error(ex)
        }
    }

    suspend fun updateProfileUser(user: User) : RepoResult<Boolean> {
        return try {
            val childUpdates = hashMapOf<String, Any>(
                "name" to user.name.toString(),
                "gender" to user.gender.toString(),
                "imgProfile" to user.imgProfile.toString(),
                "address" to user.address.toString()
            )
            user.uid?.let {
                database.child("users")
                    .child(it)
                    .updateChildren(childUpdates)
                    .await()
            }
            RepoResult.Success(true)
        } catch (ex: Exception) {
            RepoResult.Error(ex)
        }
    }

    suspend fun fetchRelationshipOfUser(uid: String) : RepoResult<List<Relationship>> = suspendCoroutine {continuation ->
        try {
            database.child("friend_request")
                .child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val listRelationship = mutableListOf<Relationship>()
                        for (it in snapshot.children) {
                            val  relationship = it.getValue(Relationship::class.java)
                            relationship?.let {
                                listRelationship.add(relationship)
                            }
                        }
                        continuation.resume(RepoResult.Success(listRelationship))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(RepoResult.Error(error.toException()))
                    }

                })
        } catch (ex: Exception) {
            RepoResult.Error(ex)
        }
    }



    suspend fun fetchListRelationship(uid: String, status: RelationshipStatus) : RepoResult<List<Relationship>> = suspendCoroutine { continuation ->
        try {
            database.child("friend_request")
                .child(uid)
                .orderByChild("status")
                .equalTo(status.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val listRelationship = mutableListOf<Relationship>()
                        for (it in snapshot.children) {
                            val relationship = it.getValue(Relationship::class.java)
                            if(relationship!= null) {
                                listRelationship.add(relationship)
                            }
                        }
                        continuation.resume(RepoResult.Success(listRelationship))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(RepoResult.Error(error.toException()))
                    }

                })
        } catch (ex: Exception) {
            continuation.resumeWithException(ex)
        }
    }

    fun observeUserRelatinshipByStatus(uid: String, status: RelationshipStatus) : StateFlow<RepoResult<List<UserRelationship>>> {
        val listUserStateFlow = MutableStateFlow<RepoResult<List<UserRelationship>>>(RepoResult.Loading)
        database.child("friend_request")
           .child(uid)
           .orderByChild("relationship")
           .equalTo(status.toString())
           .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listUser = mutableListOf<UserRelationship>()
                    for (it in snapshot.children) {
                        val user = it.getValue(UserRelationship::class.java)
                        user?.let {
                            listUser.add(user)
                        }
                    }

                    listUserStateFlow.value = RepoResult.Success(listUser)
                }
                override fun onCancelled(error: DatabaseError) {
                    listUserStateFlow.value = RepoResult.Error(error.toException())
                }
            })
        return listUserStateFlow

    }

    fun observeUserRelationship(uid: String, status: RelationshipStatus) : StateFlow<RepoResult<List<UserRelationship>>> {
        val listRelationshipStateFlow = MutableStateFlow<RepoResult<List<UserRelationship>>>(RepoResult.Loading)
        database.child("friend_request")
            .child(uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listRelationship = mutableListOf<UserRelationship>()
                    for (it in snapshot.children) {
                        Log.d("Obser", it.toString())
                        val relationship = it.getValue(UserRelationship::class.java)
                        if (relationship != null) {
                            listRelationship.add(relationship)
                        }
                    }
                    listRelationshipStateFlow.value = RepoResult.Success(listRelationship)

                }

                override fun onCancelled(error: DatabaseError) {
                    listRelationshipStateFlow.value = RepoResult.Error(error.toException())
                }
            })
        return listRelationshipStateFlow
    }



    suspend fun updateRelationshipUser(relationship: Relationship) : RepoResult<Boolean> = suspendCoroutine{
        try {
            val childUpdates = hashMapOf<String, Any>(
                "status" to relationship.status.toString()
            )
            val senderRef = database.child("friend_request")
                .child(relationship.senderId)
                .child(relationship.receiverId)

            val receiverRef = database.child("friend_request")
                .child(relationship.receiverId)
                .child(relationship.senderId)

            val senderTask = senderRef.updateChildren(childUpdates)
            val receiverTask = receiverRef.updateChildren(childUpdates)

            Tasks.await(senderTask)
            Tasks.await(receiverTask)

            RepoResult.Success(true)
        } catch (ex: Exception) {
            RepoResult.Error(ex)
        }
    }
    

    suspend fun sendMessage(message: Message, senderRoom: String, receiverRoom: String) : RepoResult<Boolean> = suspendCoroutine{
        try {
            val randomKey = database.push().key

            val taskSend = database.child("chats")
                .child(senderRoom)
                .child(randomKey!!)
                .setValue(message)

           val taskReceiver =  database.child("chats")
                .child(receiverRoom)
                .push()
                .setValue(message)

            Tasks.await(taskSend)
            Tasks.await(taskReceiver)


            RepoResult.Success(true)
        } catch (ex: Exception) {
            RepoResult.Error(ex)
        }

    }

    suspend fun checkChatRoom(roomId: String) : RepoResult<Boolean> = suspendCoroutine {continuation ->
        try {
            database.child("rooms")
                .child(roomId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            continuation.resume(RepoResult.Success(true))
                        } else {
                            continuation.resume(RepoResult.Success(false))
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(RepoResult.Error(error.toException()))
                    }
                })

        } catch (ex: Exception) {
            continuation.resume(RepoResult.Error(ex))
        }
    }

    suspend fun createRoom(room: ChatRoom, uid: String) : RepoResult<Boolean> = suspendCoroutine { continuation ->
        try {
            val randomKey = database.push().key
            database.child("rooms")
                .child(uid)
                .child(room.chatRoomUid)
                .setValue(room)
                .addOnCompleteListener {
                    continuation.resume(RepoResult.Success(true))
                }
                .addOnFailureListener {
                    continuation.resume(RepoResult.Error(it))
                }
        } catch (ex: Exception) {
            continuation.resume(RepoResult.Error(ex))
        }
    }


    suspend fun getListChatRoom(uid: String) : RepoResult<List<ChatRoom>> = suspendCoroutine { continuation ->
        try{
            database.child("rooms")
               .child(uid)
               .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val listRoom = mutableListOf<ChatRoom>()
                        for (it in snapshot.children) {
                            Log.d("DataSnapshot", it.toString())
                            val room = it.getValue(ChatRoom::class.java)
                            room?.let {
                                listRoom.add(room)
                            }
                        }
                        continuation.resume(RepoResult.Success(listRoom))
                    }
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(RepoResult.Error(error.toException()))
                    }
                })
        } catch (ex: Exception) {
            continuation.resume(RepoResult.Error(ex))
        }
    }

    suspend fun getListMessageChatRoom(chatRoomId:String) : RepoResult<List<Message>> = suspendCoroutine {continuation->
       try {
           database.child("chats")
               .child(chatRoomId)
               .addListenerForSingleValueEvent(object : ValueEventListener{
                   override fun onDataChange(snapshot: DataSnapshot) {
                       val listMessage = mutableListOf<Message>()
                       for (it in snapshot.children) {
                           val message = it.getValue(Message::class.java)
                           message?.let {
                               listMessage.add(message)
                           }
                       }
                       continuation.resume(RepoResult.Success(listMessage))
                   }
                   override fun onCancelled(error: DatabaseError) {
                       continuation.resume(RepoResult.Error(error.toException()))
                   }
               })
       } catch (ex: Exception) {
           continuation.resume(RepoResult.Error(ex))
       }
    }

     fun observeMessageChatRoomChanges(chatRoomId: String) : StateFlow<List<Message>>{
         val messageListStateFlow = MutableStateFlow<List<Message>>(emptyList())

         database.child("chats")
             .child(chatRoomId)
             .addValueEventListener(object : ValueEventListener {
                 override fun onDataChange(snapshot: DataSnapshot) {
                     val listMessage = mutableListOf<Message>()
                     for (it in snapshot.children) {
                         Log.d("FlowMessage", it.toString())
                         val message = it.getValue(Message::class.java)

                         message?.let {
                             listMessage.add(message)
                         }
                     }

                     messageListStateFlow.value = listMessage
                 }

                 override fun onCancelled(error: DatabaseError) {
                     Log.d("Database", "observe message error")
                 }
             })
         return messageListStateFlow

    }

   suspend fun updateLastMessageChatRoom(userId: String,roomId: String,message: Message) {
       try {

           val childUpdates = hashMapOf<String, Any>(
               "lastMessage" to message
           )
           database.child("rooms")
               .child(userId)
               .child(roomId)
               .updateChildren(childUpdates)
       } catch (e: Exception) {
           e.printStackTrace()
       }
   }

    suspend fun updateStatusUser(userId: String, status: String) {
        try {
            val childUpdates = hashMapOf<String, Any>(
                "status" to status
            )

            database.child("users")
                .child(userId)
                .updateChildren(childUpdates)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }









}