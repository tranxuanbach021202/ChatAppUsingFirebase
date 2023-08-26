package com.example.chatappfirebase.data.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserRelationship(
    var uidFriend: String ="",
    var name: String = "",
    var urlImgProfile : String = "",
    var relationship: String = ""
) {
    @Exclude
    fun toMap() : Map<String, Any?> {
        return hashMapOf(
            "uidFriend" to uidFriend,
            "name" to name,
            "urlImgProfile" to urlImgProfile,
            "relationship" to relationship
        )
    }
}