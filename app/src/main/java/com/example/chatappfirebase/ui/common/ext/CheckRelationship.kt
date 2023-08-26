package com.example.chatappfirebase.ui.common.ext

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.chatappfirebase.data.models.RelationshipStatus

@BindingAdapter("relationship")
fun relationshipSetText(view: TextView, relationshipStatus: String){
    val text = when(relationshipStatus) {
        RelationshipStatus.STRANGERS.toString() -> {
            "Add Friend"
        }
        RelationshipStatus.FRIEND_REQUEST.toString() -> {
            "Accept"
        }

        RelationshipStatus.FRIEND.toString() -> {
            "Friend"
        }

        RelationshipStatus.FRIEND_REQUEST_SENT.toString() -> {
            "Friend request sent"
        }

        else -> {
            "unspecified state"
        }
    }
    view.text = text

}