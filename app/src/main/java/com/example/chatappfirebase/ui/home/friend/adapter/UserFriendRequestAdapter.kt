package com.example.chatappfirebase.ui.home.friend.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappfirebase.R
import com.example.chatappfirebase.data.models.UserRelationship
import com.example.chatappfirebase.databinding.ItemUserBinding

class UserFriendRequestAdapter(
    val context: Context,
    private val onClickAccept: (UserRelationship) -> Unit
) : RecyclerView.Adapter<UserFriendRequestAdapter.UserFriendRequestViewModel>() {

    private var _users = mutableListOf<UserRelationship>()

    fun setData(users : List<UserRelationship>) {
        _users.clear()
        _users.addAll(users)
        Log.d("DataFriend", users.toString())
        notifyDataSetChanged()
    }
    inner class UserFriendRequestViewModel(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserRelationship) {
            binding.user = user
            binding.txtRelationship.setOnClickListener {
                onClickAccept(user)
            }
            binding.executePendingBindings()
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserFriendRequestViewModel {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemUserBinding>(inflater, R.layout.item_user, parent, false)
        return UserFriendRequestViewModel(binding)
    }

    override fun getItemCount(): Int {
        return _users.size
    }

    override fun onBindViewHolder(holder: UserFriendRequestViewModel, position: Int) {
        val user = _users[position]
        holder.bind(user)
    }
}