package com.example.chatappfirebase.ui.home.friend.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappfirebase.R
import com.example.chatappfirebase.data.models.UserRelationship
import com.example.chatappfirebase.databinding.ItemUserBinding

class UserFriendAdapter(
    val context: android.content.Context,
    private val onClickUserFriend: (String) -> Unit
) : RecyclerView.Adapter<UserFriendAdapter.UserListFriendViewHolder>() {

    inner class UserListFriendViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserRelationship) {
            binding.user = user
            binding.executePendingBindings()


            binding.root.setOnClickListener {
                onClickUserFriend(user.uidFriend)
            }
        }

        init {
            binding.txtRelationship.visibility = View.GONE
        }
    }

    private val _users = mutableListOf<UserRelationship>()
    fun setData(users : List<UserRelationship>) {
        _users.clear()
        _users.addAll(users)
        Log.d("AdapterFriend", _users.toString())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListFriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemUserBinding>(inflater, R.layout.item_user, parent, false)
        return UserListFriendViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return _users.size
    }

    override fun onBindViewHolder(holder: UserListFriendViewHolder, position: Int) {
        val user = _users[position]
        holder.bind(user)


    }
}