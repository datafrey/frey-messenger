package com.datafrey.freymessenger.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.loadImage
import com.datafrey.freymessenger.model.Message
import kotlinx.android.synthetic.main.message_item.view.*

class MessageAdapter
    : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MessageViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.message_item, parent, false))

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) =
        holder.fillWithMessage(getItem(position))

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val context = itemView.context

        var outcomingPhotoImageView: ImageView = itemView.outcomingPhotoImageView
        var outcomingTextTextView: TextView = itemView.outcomingTextTextView
        var incomingPhotoImageView: ImageView = itemView.incomingPhotoImageView
        var incomingTextTextView: TextView = itemView.incomingTextTextView

        fun fillWithMessage(message: Message) {
            val isTextMessage = message.imageUrl == null
            val isPhotoMessage = !isTextMessage

            outcomingPhotoImageView.isVisible = isPhotoMessage && message.isMine!!
            outcomingTextTextView.isVisible = isTextMessage && message.isMine!!
            incomingPhotoImageView.isVisible = isPhotoMessage && !message.isMine!!
            incomingTextTextView.isVisible = isTextMessage && !message.isMine!!

            if (isTextMessage && message.isMine!!) {
                outcomingTextTextView.text = message.text
            } else if (isTextMessage && !message.isMine!!) {
                incomingTextTextView.text = message.text
            } else if (isPhotoMessage && message.isMine!!) {
                context.loadImage(message.imageUrl!!, outcomingPhotoImageView)
            } else {
                context.loadImage(message.imageUrl!!, incomingPhotoImageView)
            }
        }

    }

}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {

    override fun areItemsTheSame(oldItem: Message, newItem: Message) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Message, newItem: Message) =
        oldItem == newItem

}