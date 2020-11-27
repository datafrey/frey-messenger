package com.datafrey.freymessenger.adapters

import androidx.recyclerview.widget.DiffUtil
import com.datafrey.freymessenger.model.Message

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {

    override fun areItemsTheSame(oldItem: Message, newItem: Message) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Message, newItem: Message) =
        oldItem == newItem
}