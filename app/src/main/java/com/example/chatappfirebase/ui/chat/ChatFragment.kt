package com.example.chatappfirebase.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.example.chatappfirebase.constants.Constants
import com.example.chatappfirebase.data.models.ChatRoom
import com.example.chatappfirebase.data.models.Message
import com.example.chatappfirebase.data.models.TypeMessage
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.databinding.FragmentChatBinding
import com.example.chatappfirebase.utils.RepoResult
import com.google.firebase.auth.FirebaseAuth
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding

    @Inject
    lateinit var auth: FirebaseAuth
    private val args: ChatFragmentArgs by navArgs()
    private val viewModel: ChatViewModel by viewModels()

    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private lateinit var receiverUser: User
    private lateinit var userCurrent: User
    private var checkChatRoom : Boolean? = false
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupSendMessage()
        obseverInfoUser()
        setupMessageAdapter()
        setupButtonFile()
        observerUrlImage()

        binding.voiceCall.setOnClickListener {
            setVoiceCall(args.userId)
            binding.zegocloudVoiceCall.performClick()
        }
        binding.videoCall.setOnClickListener {
            setVideoCall(args.userId)
            binding.zegocloudVideoCall.performClick()
        }
    }

    fun setupViews() {
        val userId = args.userId
        userCurrent = args.userCurrent
        senderRoom = auth.currentUser?.uid!! + userId
        receiverRoom = userId + auth.currentUser?.uid!!
        obseverCheckChatRoom()
        fetchInfoUser(userId)
        observerDataMessage(senderRoom)
    }

    fun setupMessageAdapter(){
        messageAdapter = MessageAdapter(requireContext(), auth.currentUser?.uid!!)
        binding.rvMessage.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvMessage.adapter = messageAdapter
    }



    private fun fetchInfoUser(userId: String) {
        viewModel.fetchInfoUser(userId)
    }

    private fun obseverInfoUser() {
        viewModel.fetchInfoUserResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RepoResult.Success -> {
                    setupViewUser(it.data)
                }
                is RepoResult.Error -> {
                    Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is RepoResult.Loading -> {

                }
            }
        }
        )
    }

    private fun setupViewUser(user: User) {
        receiverUser = user
        binding.txtUserName.text = user.name
        binding.txtStatusUser.text = user.status
        Glide.with(this)
            .load(user.imgProfile)
            .into(binding.ivAvatarUser)
        checkChatRoom(senderRoom)

        messageAdapter.setUserReceiver(user)
    }

    private fun setupSendMessage() {

        binding.btnSendMessage.setOnClickListener {
            val content = binding.edMessage.text.toString()


            val currentTime = SimpleDateFormat("hh:mm a")
            val calendar = Calendar.getInstance()
            val time = currentTime.format(calendar.time).toString()

            val senderId = auth.currentUser?.uid!!

            val message = Message(senderId, content, TypeMessage.TEXT.toString(),time)
            sendMessage(message)
            updateLastMessage(message)
        }
    }


    private fun sendMessage(message: Message) {
        viewModel.sendMessage(message, senderRoom, receiverRoom)
    }


    private fun checkChatRoom(roomId: String) {
        viewModel.checkChatRoom(roomId)
    }

    private fun obseverCheckChatRoom() {
        viewModel.checkChatRoomResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RepoResult.Success -> {
                   checkChatRoom = it.data
                    Log.d("CreateChatRoom", it.data.toString())
                    if (!it.data) {
                        val message = Message()
                        createChatRoom(message!!)
                    }
                }

                is RepoResult.Error -> {
                    Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT)
                        .show()
                    Log.e("CreateChatRoom", it.exception.toString())
                }

                is RepoResult.Loading -> {
                }
            }
        })
    }


    private fun createChatRoom(message: Message) {
        // tạo room của người gửi
        val roomSender = ChatRoom(receiverUser?.uid!!, receiverUser?.imgProfile!!, senderRoom, receiverUser?.name!!, message)
        viewModel.createChatRoom(roomSender, auth.currentUser?.uid!!)

        // tạo room của người nhận
        val roomReceiver = ChatRoom(userCurrent.uid!!,userCurrent.imgProfile!!, receiverRoom, userCurrent.name!!, message)
        viewModel.createChatRoom(roomReceiver, receiverUser?.uid!!)
    }


    private fun observerDataMessage(roomId: String) {
        viewModel.observeMessageChatRoomChanges(roomId)

        viewModel.messageListLiveData.observe(viewLifecycleOwner, Observer {
            messageAdapter.setData(it)
        })
    }



    private fun setupButtonFile() {
        binding.btnFile.setOnClickListener {
            photoPicker()
        }
    }

    private fun observerUrlImage() {
        viewModel.uploadImageAvatarResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RepoResult.Success -> {
                    Log.d("uploadImageAvatar", "success")
                    val currentTime = SimpleDateFormat("hh:mm a")
                    val calendar = Calendar.getInstance()
                    val time = currentTime.format(calendar.time).toString()
                    val message = Message(auth.currentUser?.uid!!, it.data, TypeMessage.IMAGE.toString(), time)
                    sendMessage(message)
                }
                is RepoResult.Error -> {
                    Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT)
                      .show()
                }
                is RepoResult.Loading -> {
                }
            }
        })
    }

    private fun updateLastMessage(message: Message) {
        Log.d("Testt", "UpdateLastMessage")
        //update last message of receiver
        viewModel.updateLastMessage(receiverUser.uid!!, receiverRoom, message)

        // update last message of sender
        val messageSender = Message(message.senderId, "you: " + message.content, message.type, message.timestamp)
        viewModel.updateLastMessage(auth.currentUser?.uid!!, senderRoom, messageSender)

    }

    // pick image
    private fun photoPicker(){
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")

            viewModel.uploadImageAvatar(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    fun setVideoCall(s: String) {
        binding.zegocloudVideoCall.setIsVideoCall(true)
        binding.zegocloudVideoCall.setResourceID("zego_uikit_call")
        binding.zegocloudVideoCall.setInvitees(Collections.singletonList(ZegoUIKitUser(s)))
    }

    fun setVoiceCall(s: String) {
        binding.zegocloudVoiceCall.setIsVideoCall(false)
        binding.zegocloudVoiceCall.setResourceID("zego_uikit_call")
        binding.zegocloudVoiceCall.setInvitees(Collections.singletonList(ZegoUIKitUser(s)))
    }



}