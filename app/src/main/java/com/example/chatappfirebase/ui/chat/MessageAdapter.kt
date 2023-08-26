package com.example.chatappfirebase.ui.chat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatappfirebase.R
import com.example.chatappfirebase.data.models.Message
import com.example.chatappfirebase.data.models.TypeMessage
import com.example.chatappfirebase.data.models.User
import com.example.chatappfirebase.databinding.ItemMessageReceiverBinding
import com.example.chatappfirebase.databinding.ItemMessageSendBinding
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class MessageAdapter(
    private val context: Context,
    private val uidAuth: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_SENDER = 1
    val ITEM_RECEIVER = 2
    private val _listMessage = mutableListOf<Message>()

    fun setData(listMessage: List<Message>) {
        _listMessage.clear()
        _listMessage.addAll(listMessage)
        notifyDataSetChanged()
    }

    private var _userReceiver = User()
    fun setUserReceiver(userReceiver: User) {
        _userReceiver = userReceiver
    }
    inner class SendMessageViewHolder(private val binding: ItemMessageSendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            if (message.type == TypeMessage.TEXT.toString()) {
                binding.viewMessageText.visibility = View.VISIBLE
                binding.txtTime.text = message.timestamp
                binding.txtContent.text = message.content
            } else if (message.type == TypeMessage.IMAGE.toString()) {
                binding.viewMessageImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(message.content)
                    .error(R.drawable.no_avatar)
                    .into(binding.imageview)
            }

        }
    }

    inner class ReceiveMessageViewHolder(private val binding: ItemMessageReceiverBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            if (message.type == TypeMessage.TEXT.toString()) {
                binding.viewMessageText.visibility = View.VISIBLE
                binding.txtContent.text = message.content
                binding.txtTime.text = message.timestamp
            } else if (message.type == TypeMessage.IMAGE.toString()) {
                binding.viewMessageImage.visibility = View.VISIBLE
                Glide.with(context)
                   .load(message.content)
                   .error(R.drawable.no_avatar)
                   .into(binding.imageview)
            }
        }
        init {
            binding.userReceiver = _userReceiver
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == ITEM_SENDER) {
            val binding_send = ItemMessageSendBinding.inflate(inflater, parent, false)
            return SendMessageViewHolder(binding_send)
        } else {
            val binding_receiver = ItemMessageReceiverBinding.inflate(inflater, parent, false)
            return ReceiveMessageViewHolder(binding_receiver)
        }
    }

    override fun getItemCount(): Int {
       return _listMessage.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = _listMessage[position]
        if (holder.javaClass == SendMessageViewHolder::class.java) {
            val viewHolder = holder as SendMessageViewHolder
            viewHolder.bind(message)
        } else {
            val viewHolder = holder as ReceiveMessageViewHolder
            viewHolder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (uidAuth == _listMessage[position].senderId) {
            return ITEM_SENDER
        } else {
            return ITEM_RECEIVER
        }
    }
}