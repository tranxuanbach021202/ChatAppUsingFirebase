package com.example.chatappfirebase.ui.common.ext

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.chatappfirebase.R

@BindingAdapter("image")
fun loadImage(view: ImageView, imageUrl: String) {

    Log.d("Image", imageUrl)
    if (!imageUrl.isEmpty()) {
        Log.d("Image", "ko null")
        Glide.with(view)
            .load(imageUrl)
            .error(R.drawable.no_avatar)
            .into(view)
    } else {
        Log.d("Image", "null")
    }
}