package com.datafrey.freymessenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.databinding.MessageItemBinding
import com.datafrey.freymessenger.model.Message

class MessageAdapter
    : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MessageViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.message_item, parent, false))

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) =
        holder.bindMessage(getItem(position))

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = MessageItemBinding.bind(itemView)

        fun bindMessage(message: Message) {
            binding.message = message
            binding.isIncomingImageMessage = message.text == null && !message.isMine!!
            binding.isIncomingTextMessage = message.imageUrl == null && !message.isMine!!
            binding.isOutcomingImageMessage = message.text == null && message.isMine!!
            binding.isOutcomingTextMessage = message.imageUrl == null && message.isMine!!

            binding.executePendingBindings()
        }
    }
}