package com.example.chatappfirebase.ui.home.message.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatappfirebase.R
import com.example.chatappfirebase.data.models.ChatRoom
import com.example.chatappfirebase.databinding.ItemChatRecentBinding
import com.example.chatappfirebase.databinding.ItemUserBinding

class ChatRoomAdapter(
    val context: Context,
    private val onClickChatRoom: (String) -> Unit
) : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    private val _listChatRoom = mutableListOf<ChatRoom>()
    fun setData(listChatRoom: List<ChatRoom>) {
        _listChatRoom.clear()
        _listChatRoom.addAll(listChatRoom)
        notifyDataSetChanged()
    }
    inner class ChatRoomViewHolder(private val binding: ItemChatRecentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatRoom: ChatRoom) {
            binding.room = chatRoom
            binding.executePendingBindings()


            binding.root.setOnClickListener{
                onClickChatRoom(chatRoom.receiverId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemChatRecentBinding>(inflater, R.layout.item_chat_recent, parent, false)
        return ChatRoomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return _listChatRoom.size
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val chatRoom = _listChatRoom[position]
        holder.bind(chatRoom)
    }
}