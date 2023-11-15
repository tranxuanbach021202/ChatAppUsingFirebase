package com.example.chatappfirebase.ui.sigup

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.chatappfirebase.BuildConfig
import com.example.chatappfirebase.databinding.ActivitySigupBinding
import com.example.chatappfirebase.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import uk.co.jakebreen.sendgridandroid.SendGrid
import uk.co.jakebreen.sendgridandroid.SendGridMail
import uk.co.jakebreen.sendgridandroid.SendTask
import kotlin.random.Random


@AndroidEntryPoint
class SigupActivity : AppCompatActivity(){

    val TAG = "SigupActivity"

    private lateinit var binding: ActivitySigupBinding
    val viewModel : SigupViewModel by viewModels()
    private var Otp: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSigup()
        setupObseverSigup()
    }

    fun setupSigup() {
        binding.btnSigup.setOnClickListener {
            if (checkOTP()) {
                Log.d(TAG, "check otp successfully")
                createAccount()
            } else {
                Toast.makeText(this, "Please enter valid OTP", Toast.LENGTH_SHORT).show()
            }
        }


        binding.txtSendOtp.setOnClickListener {
            Log.d(TAG, "click send otp")
            val email = binding.edEmail.text.toString()
            sendOTP(email)
        }
    }

    fun createAccount() {
        val email = binding.edEmail.text.toString()
        val password = binding.edPassword.text.toString()
        val confirmPassword = binding.edConfirmPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                viewModel.sigup(email, password)
            } else {
                Toast.makeText(this, "Confirm password failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Email, Password, Confirm password null", Toast.LENGTH_SHORT).show()
        }
    }

    fun setupObseverSigup() {
        viewModel.sigupResult.observe(this, Observer { data ->
            if (data == true) {
                navigationToScreen()
            } else {
                Toast.makeText(this, "Sigup failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun navigationToScreen() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun sendOTP(email: String) {
        Otp = generateOTP()
        val sendGrid = SendGrid.create(BuildConfig.SENDGRID_API_KEY)
        val mail = SendGridMail()
        mail.addRecipient(email, email)
        mail.setFrom("tranxuanbachnt@gmail.com", "tran bach")
        mail.setSubject("OTP")
        mail.setHtmlContent("html/body")
        mail.setTemplateId(BuildConfig.SENDGRID_TEMPLATE_ID)
        val tempplateData = JSONObject()
        tempplateData.put("name", email)
        tempplateData.put("otp", Otp)
        mail.setDynamicTemplateData(tempplateData)
        val task = SendTask(sendGrid)
        val reponse = task.send(mail)

        if (reponse.isSuccessful) {
            Log.d(TAG, "send email success")
            startCountdown()
        }
    }

    fun generateOTP(): String {
        val otpLength = 6
        val otpString = StringBuilder(otpLength)
        repeat(otpLength) {
            val randomDigit = Random.nextInt(0, 10)
            otpString.append(randomDigit)
        }

        return otpString.toString()
    }

    fun checkOTP(): Boolean {
        val otpEditText = binding.edOtp.text.toString()
        if (Otp == otpEditText) {
            return true
        }
        return false
    }

    private fun startCountdown() {
        val countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                Log.d(TAG, "onTick")
                binding.txtCountDownTimer.text = secondsRemaining.toString()


                if (secondsRemaining == 0L) {
                    binding.txtCountDownTimer.visibility = View.GONE
                    cancel()
                }
            }
            override fun onFinish() {
                binding.txtCountDownTimer.visibility = View.GONE
            }
        }
        countDownTimer.start()
    }





}