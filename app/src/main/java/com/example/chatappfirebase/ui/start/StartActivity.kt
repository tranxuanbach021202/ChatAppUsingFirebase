package com.example.chatappfirebase.ui.start

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.chatappfirebase.R
import com.example.chatappfirebase.databinding.ActivityStartBinding
import com.example.chatappfirebase.ui.login.LoginActivity
import com.example.chatappfirebase.ui.sigup.SigupActivity

/*
  StartActivity là screen đầu khi khởi động app
  có 2 button để chọn login hoặc sigup
 */
class StartActivity : AppCompatActivity() {

    val TAG = "StartActivity"

    private var isActivityResultRegistered = false

    private lateinit var binding: ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // triển khai 2 button để khởi tạo màn tương ứng
        setupBtn()

    }



    fun setupBtn() {
        binding.btnLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, SigupActivity::class.java)
            startActivity(intent)
        }
    }

    fun photoPicker() {
        // Registers a photo picker activity launcher in single-select mode.


        // Launch the photo picker and let the user choose only images.

    }







}