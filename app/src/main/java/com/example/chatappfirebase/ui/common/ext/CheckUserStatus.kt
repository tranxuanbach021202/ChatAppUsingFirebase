package com.example.chatappfirebase.ui.common.ext

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.chatappfirebase.data.models.UserStatus

@BindingAdapter("status")
fun setStatus(view: TextView, status: String) {
    if (status == UserStatus.ONLINE.toString()) {
        view.text = "Active"
    } else {
        view.text = "Offline"
    }
}