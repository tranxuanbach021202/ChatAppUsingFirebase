package com.example.chatappfirebase.data.models

import com.google.firebase.database.Exclude

data class Message(
    var senderId: String = "",
    var content: String = "",
    var type: String = "",
    var timestamp: String = ""
) {
    @Exclude
    fun toMap() : Map<String, Any?> {
        return mapOf(
            "senderId" to senderId,
            "content" to content,
            "type" to type,
            "timestamp" to timestamp
        )
    }
}