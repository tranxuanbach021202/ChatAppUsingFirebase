package com.example.chatappfirebase.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chatappfirebase.BuildConfig
import com.example.chatappfirebase.R
import com.example.chatappfirebase.data.models.UserStatus
import com.example.chatappfirebase.databinding.ActivityHomeBinding
import com.example.chatappfirebase.utils.RepoResult
import com.google.firebase.auth.FirebaseAuth
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var controller: NavController

    private val viewModel: HomeViewModel by viewModels()
    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var uidCurrent: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uidCurrent = auth.currentUser?.uid!!
        setupNav()

        viewModel.fetchInfoUserCurrent(auth.currentUser?.uid!!)
        obseverInfoUserCurrent()

        videoCall(auth.currentUser?.uid!!)


    }

    override fun onStart() {
        viewModel.updateStatusUser(auth.currentUser?.uid!!, UserStatus.ONLINE.toString())
        super.onStart()
    }

    fun setupNav() {
        controller = findNavController(R.id.home_container_fragment)
        binding.navBottom.setupWithNavController(controller)


        controller.addOnDestinationChangedListener{  _, destination, _ ->
            if (destination.id == R.id.chatFragment) {
                binding.navBottom.visibility = View.GONE
            } else {
                binding.navBottom.visibility = View.VISIBLE
            }
        }
    }

    fun obseverInfoUserCurrent() {
        viewModel.infoUserCurrent.observe(this) {
            when (it) {
                is RepoResult.Success -> {
                    viewModel.userCurrent.value = it.data
                }
                is RepoResult.Error -> {

                }
                is RepoResult.Loading -> {

                }
            }
        }
    }

    private fun videoCall(userId: String) {
        val userID = userId // yourUserID, userID should only contain numbers, English characters, and '_'.
        val userName = "aaaa"   // yourUserName

        val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
        val notificationConfig =  ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        ZegoUIKitPrebuiltCallInvitationService.init(application, BuildConfig.APP_ID_ZEGOCLOUD, BuildConfig.APP_SIGN_ZEGOCLOUD, userID, userName,callInvitationConfig);
    }

    override fun onStop() {
        viewModel.updateStatusUser(uidCurrent, UserStatus.OFFLINE.toString())
        super.onStop()
    }

}