package com.example.chatappfirebase.ui.home.friend

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappfirebase.data.models.Relationship
import com.example.chatappfirebase.data.models.RelationshipStatus
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.data.models.UserRelationship
import com.example.chatappfirebase.databinding.FragmentFriendBinding
import com.example.chatappfirebase.ui.home.friend.adapter.UserFriendAdapter
import com.example.chatappfirebase.ui.home.friend.adapter.UserFriendRequestAdapter
import com.example.chatappfirebase.ui.home.friend.adapter.UserListAdapter
import com.example.chatappfirebase.utils.RepoResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FriendFragment : Fragment() {

    @Inject
    lateinit var auth: FirebaseAuth
    lateinit var binding: FragmentFriendBinding
    private val viewModel : FriendViewModel by viewModels()
    private lateinit var userListFriendRequestAdapter: UserFriendRequestAdapter
    private lateinit var userListFriendAdapter : UserFriendAdapter
    private lateinit var userListAdapter: UserListAdapter
    private lateinit var userCurrent: User

    private var userRelationships: List<UserRelationship> = mutableListOf()
    private val controller: NavController by lazy { findNavController() }
    val TAG = "FriendFragment"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = FragmentFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setup()
        fetchData()
        obseverInfoUserCurrent()
        observerUserRelationship()
        viewModel.observeUserRelationship(auth.currentUser?.uid!!, RelationshipStatus.STRANGERS)
        viewModel.observeListUser(auth.currentUser?.uid!!)
        observerUserRelationshipByFriend()
        observerUserRelationshipByStatus()
        observerUserRelationshipByFriendRequest()


    }

    fun setup(){
        setupUserListFriendRequestAdapter()
        setupUserListFriend()
        setupUserListAdapter()
        setupSearchUser()

    }

    fun setupSearchUser() {


        var viewFriendVisibility = View.GONE
        var viewFriendRequestVisibility = View.GONE

        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.edSearchUser.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                viewFriendVisibility = binding.viewFriend.visibility
                viewFriendRequestVisibility= binding.viewFriendRequest.visibility

                binding.viewListUser.visibility = View.VISIBLE
                binding.viewFriend.visibility = View.GONE
                binding.viewFriendRequest.visibility = View.GONE
            } else {
                binding.viewListUser.visibility = View.GONE
                binding.viewFriend.visibility = viewFriendVisibility
                binding.viewFriendRequest.visibility = viewFriendRequestVisibility
            }
        }
        
        binding.viewFragmentFriend.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                binding.edSearchUser.clearFocus()
                return@setOnTouchListener true
            }

            return@setOnTouchListener false
        }

        binding.edSearchUser.addTextChangedListener (object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(!s.isNullOrEmpty()) {

                    binding.txtSuggest.visibility = View.GONE
                    val list = searchUserRelationship(s.toString(), userRelationships)
                    userListAdapter.setData(list)
                } else {
                    binding.txtSuggest.visibility = View.VISIBLE
                    userListAdapter.setData(userRelationships)
                }
            }

        })
    }

    fun searchUserRelationship(key: String, list : List<UserRelationship>) : List<UserRelationship> {
        val result = mutableListOf<UserRelationship>()
        for (it in list) {
            if (it.name!!.contains(key, ignoreCase = true)) {
                result.add(it)
            }
        }
        return result
    }


    fun setupUserListFriendRequestAdapter() {

        fun onClickAccept(userRelationship: UserRelationship) {
            Log.d(TAG, userRelationship.toString())

            // update relationship sender status to friend
            userRelationship.relationship = RelationshipStatus.FRIEND.toString()
            viewModel.updateUserRelationship(auth.currentUser?.uid!!, userRelationship)

            // update relationship receiver status to friend
            val userRelationshipReceiver = UserRelationship(
                auth.currentUser?.uid!!,
                userCurrent.name!!,
                userCurrent.imgProfile!!,
                RelationshipStatus.FRIEND.toString()
            )
            viewModel.updateUserRelationship(
                userRelationship.uidFriend,
                userRelationshipReceiver
            )
        }

        userListFriendRequestAdapter = UserFriendRequestAdapter(requireContext(), ::onClickAccept)
        binding.rvUserListFriendRequest.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvUserListFriendRequest.adapter = userListFriendRequestAdapter
    }

    fun setupUserListFriend(){

        fun onClickUserFriend(userId: String) {
            navigationToScreen(userId, userCurrent!!)
        }

        userListFriendAdapter = UserFriendAdapter(requireContext(), ::onClickUserFriend)
        binding.rvUserListFriend.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvUserListFriend.adapter = userListFriendAdapter
    }

    fun setupUserListAdapter() {
        fun onClickBtn(userRelationship: UserRelationship) {
            if (userRelationship.relationship == RelationshipStatus.FRIEND_REQUEST.toString()) {

                // update relationship sender status to friend
                userRelationship.relationship = RelationshipStatus.FRIEND.toString()
                viewModel.updateUserRelationship(auth.currentUser?.uid!!, userRelationship!!)

                // update relationship receiver status to friend
                val userRelationshipReceiver = UserRelationship(
                    auth.currentUser?.uid!!,
                    userCurrent.name!!,
                    userCurrent.imgProfile!!,
                    RelationshipStatus.FRIEND.toString()
                )
                viewModel.updateUserRelationship(userRelationship.uidFriend, userRelationshipReceiver)
            } else if (userRelationship.relationship == RelationshipStatus.STRANGERS.toString()) {

                // create relationship of sender
                userRelationship.relationship = RelationshipStatus.FRIEND_REQUEST_SENT.toString()
                viewModel.createRelationship(auth.currentUser?.uid!!, userRelationship!!)

                // create relationship of receiver
                val userRelationshipReceiver = UserRelationship(
                    auth.currentUser?.uid!!,
                    userCurrent.name!!,
                    userCurrent.imgProfile!!,
                    RelationshipStatus.FRIEND_REQUEST.toString()
                )
                viewModel.createRelationship(userRelationship.uidFriend, userRelationshipReceiver)
            } else if (userRelationship.relationship == RelationshipStatus.PENDING.toString()) {

                // unfriend
                userRelationship.relationship = RelationshipStatus.STRANGERS.toString()
                viewModel.updateUserRelationship(auth.currentUser?.uid!!, userRelationship!!)

                // unfriend
            }
        }

        // setup adapter
        userListAdapter = UserListAdapter(requireContext(), ::onClickBtn)
        binding.rvUserList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvUserList.adapter = userListAdapter
    }

    fun fetchData() {
        viewModel.fetchInfoUserCurrent(auth.currentUser?.uid!!)
    }

    fun obseverInfoUserCurrent() {
        viewModel.userCurrent.observe(viewLifecycleOwner, Observer {
            when(it) {
                is RepoResult.Success -> {
                    Log.d(TAG, it.data.toString())
                    userCurrent = it.data
                }
                is RepoResult.Loading -> {

                }
                is RepoResult.Error -> {
                    Log.e(TAG, it.exception.message.toString())
                }
            }
        })
    }

    fun observerUserRelationship() {
        viewModel.userRelationshipCheck.observe(viewLifecycleOwner, Observer {
            userListAdapter.setData(it)
            userRelationships = it
        })
    }

    fun observerUserRelationshipByFriend() {
        viewModel.observerUserRelationshipByFriend(auth.currentUser?.uid!!)
        viewModel.userRelationshipByFriend.observe(viewLifecycleOwner, Observer {
            Log.d("Data status friend", it.toString())
            if (it.isEmpty()) {
                binding.viewFriend.visibility = View.GONE
            } else {
                binding.viewFriend.visibility = View.VISIBLE
            }
            userListFriendAdapter.setData(it)
        })
    }

    fun observerUserRelationshipByFriendRequest() {
        viewModel.observerUserRelationshipByFriendRequest(auth.currentUser?.uid!!)
        viewModel.userRelationshipByFriendRequest.observe(viewLifecycleOwner, Observer {
            Log.d("Data status friend request", it.toString())
            if (it.isEmpty()) {
                binding.viewFriendRequest.visibility = View.GONE
            } else {
                binding.viewFriendRequest.visibility = View.VISIBLE
            }
            userListFriendRequestAdapter.setData(it)
        })
    }

    fun observerUserRelationshipByStatus() {
        viewModel.observerUserRelationshipByStatus(auth.currentUser?.uid!!, RelationshipStatus.FRIEND_REQUEST_SENT)
        viewModel.userRelationshipByStatus.observe(viewLifecycleOwner,  Observer {
            Log.d("Data status", it.toString())
        })
    }




    fun navigationToScreen(userId: String, userCurrent: User) {
        val directions = FriendFragmentDirections.actionFriendFragmentToChatFragment(userId, userCurrent)
        controller.navigate(directions)
    }








}