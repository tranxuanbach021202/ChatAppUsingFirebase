package com.example.chatappfirebase.ui.home.message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappfirebase.utils.RepoResult
import com.example.chatappfirebase.data.models.Relationship
import com.example.chatappfirebase.data.models.RelationshipStatus
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.databinding.FragmentMessageBinding
import com.example.chatappfirebase.ui.common.LoadingDialog
import com.example.chatappfirebase.ui.home.HomeViewModel
import com.example.chatappfirebase.ui.home.message.adapter.ChatRoomAdapter
import com.example.chatappfirebase.ui.home.friend.adapter.UserListAdapter
import com.example.chatappfirebase.ui.home.message.adapter.UserListOnlineAdapter
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessageFragment : Fragment(){

    private val TAG = "MessageFragment"
    lateinit var binding: FragmentMessageBinding
    private lateinit var userListOnlineAdapter: UserListOnlineAdapter
    private lateinit var chatRoomAdapter: ChatRoomAdapter
    private lateinit var userCurrent: User
    private lateinit var loadingDialog : LoadingDialog
    private val viewModel : HomeMessageViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val controller: NavController by lazy { findNavController() }
    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserOnlineAdapter()
        setupChatRoomAdapter()
        fetchData()
        obseverListFriendOnline()
        obseverListChatRoom()
        obseverInfoUserCurrent()

        loadingDialog = LoadingDialog(requireContext())
        obseverLoading()
    }

    fun fetchData() {
        viewModel.fetchData(auth.currentUser?.uid!!)
    }



    fun setupUserOnlineAdapter(){
        userListOnlineAdapter = UserListOnlineAdapter(requireContext(), :: onClickFriendOnline)
        binding.rvFriendOnline.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFriendOnline.adapter = userListOnlineAdapter
    }

    fun onClickFriendOnline(uid: String) {

    }

    fun setupChatRoomAdapter(){
        fun onClickChatRoom(uid: String) {
            navigationToSscreen(uid, userCurrent)
        }
        chatRoomAdapter = ChatRoomAdapter(requireContext(), :: onClickChatRoom)
        binding.rvChatRoom.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvChatRoom.adapter = chatRoomAdapter
    }




    fun obseverListFriendOnline() {
        viewModel.listFriendOnlineResult.observe(viewLifecycleOwner, Observer {
            when(it) {
                is RepoResult.Loading -> {
                    Log.d(TAG, "Loading")
                }
                is RepoResult.Success -> {
                   if (it.data.isEmpty()) {
                       binding.layoutCurrentlyActive.visibility = View.GONE
                   } else {
                       Log.d("Currently active", it.data.toString())
                       binding.layoutCurrentlyActive.visibility = View.VISIBLE
                       userListOnlineAdapter.setData(it.data)
                   }

                }
                is RepoResult.Error -> {
                    Log.e(TAG, it.exception.message.toString())
                }
            }
        })
    }


    fun obseverListChatRoom(){
        viewModel.listChatRoomResult.observe(viewLifecycleOwner, Observer {
            when(it) {
                is RepoResult.Loading -> {
                    Log.d(TAG, "Loading")
                }
                is RepoResult.Success -> {
                    val a = it.data
                    Log.d(TAG, a.toString())
                    if (!it.data.isEmpty()) {
                        binding.layoutRecents.visibility = View.VISIBLE
                        chatRoomAdapter.setData(it.data)
                    } else {

                    }
                }
                is RepoResult.Error -> {
                    Log.e(TAG, it.exception.message.toString())
                }
            }
        })
    }

    fun obseverInfoUserCurrent(){
        viewModel.userCurrent.observe(viewLifecycleOwner, Observer{
            when(it) {
                is RepoResult.Loading -> {
                    Log.d(TAG, "Loading")
                }
                is RepoResult.Success -> {
                    userCurrent = it.data
                }
                is RepoResult.Error -> {
                    Log.e(TAG, it.exception.message.toString())
                }
            }
        })
    }

    fun obseverLoading() {
        viewModel.loading.observe(viewLifecycleOwner, Observer{
            if (it) {
                loadingDialog.startLoadingDialog()
            } else {
                loadingDialog.dismissDialog()
            }
        })
    }

    private fun navigationToSscreen(userId: String, userCurrent: User) {
        val directions = MessageFragmentDirections.actionMessageFragmentToChatFragment(userId, userCurrent)
        controller.navigate(directions)
    }








}