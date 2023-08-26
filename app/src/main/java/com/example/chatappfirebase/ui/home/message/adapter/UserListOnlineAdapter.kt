package com.example.chatappfirebase.ui.home.message.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.databinding.ItemCurrentlyActiveBinding

class UserListOnlineAdapter(
    val context: Context,
    private val onClickUser: (String) -> Unit
) : RecyclerView.Adapter<UserListOnlineAdapter.UserListOnlineViewHolder>(){

    private val _users = mutableListOf<User>()

    fun setData(users : List<User>) {
        _users.clear()
        _users.addAll(users)
        notifyDataSetChanged()
    }
    inner class UserListOnlineViewHolder(private val binding: ItemCurrentlyActiveBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                onClickUser(user.uid.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListOnlineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCurrentlyActiveBinding.inflate(inflater, parent, false)
        return UserListOnlineViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return _users.size
    }
    override fun onBindViewHolder(holder: UserListOnlineViewHolder, position: Int) {
        val user = _users[position]
        holder.bind(user)
    }
}