package com.example.chatappfirebase.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.chatappfirebase.databinding.ActivityLoginBinding
import com.example.chatappfirebase.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupLogin()
        setupObseverLogin()
    }

    private fun setupLogin() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Email, Password must not be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(email: String, password: String) {
        viewModel.login(email, password)
    }

    private fun setupObseverLogin() {
        viewModel.loginResult.observe(this, Observer { data ->
            if (data == true) {
                navigationToScreen()
            } else {
                Toast.makeText(this, "Mật khẩu sai, vui nhập lại!", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun navigationToScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }





}