package com.example.chatappfirebase.data.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Relationship(
    var senderId : String ="",
    var receiverId : String ="",
    var status: RelationshipStatus = RelationshipStatus.STRANGERS
) {

    @Exclude
    fun toMap() : Map<String, Any?> {
        return mapOf(
            "senderId" to senderId,
            "receiverId" to receiverId,
            "status" to status
        )

    }

}