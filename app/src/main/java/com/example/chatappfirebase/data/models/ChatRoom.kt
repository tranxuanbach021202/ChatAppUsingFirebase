package com.example.chatappfirebase.data.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class ChatRoom(
    var receiverId: String = "",
    var imgChatRoom: String = "",
    var chatRoomUid: String = "",
    var receiverName: String = "",
    var lastMessage: Message? = null
) {
    @Exclude
    fun toMap() : Map<String, Any?> {
        return mapOf(
            "receiverId" to receiverId,
            "chatRoomUid" to chatRoomUid,
            "receiverName" to receiverName,
            "lastMessage" to lastMessage
        )

    }

}