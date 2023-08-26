package com.example.chatappfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatappfirebase.ui.start.StartActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupStartActivity()
    }


    fun setupStartActivity() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
    }
}