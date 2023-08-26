package com.example.chatappfirebase.ui.home.friend.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappfirebase.R
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.data.models.UserRelationship
import com.example.chatappfirebase.databinding.ItemUserBinding

class UserListAdapter(
    val context: Context,
    private val onClickAddFriend: (UserRelationship) -> Unit
) :  RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private var _users = emptyList<User>()
    private var _usersRealtionship = emptyList<UserRelationship>()

    fun setData(usersRealtionship: List<UserRelationship>) {
        _usersRealtionship = usersRealtionship
        Log.d("Adapter", _usersRealtionship.toString())
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemUserBinding>(inflater, R.layout.item_user, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = _usersRealtionship[position]
       holder.bind(user)

    }

    override fun getItemCount(): Int {
        return _usersRealtionship.size
    }



    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserRelationship) {
            binding.user = user
            binding.executePendingBindings()

            binding.txtRelationship.setOnClickListener {
                binding.txtRelationship.text = "Friend request sent"
                onClickAddFriend(user)
            }
        }
    }
}