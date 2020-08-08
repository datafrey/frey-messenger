package com.datafrey.freymessenger.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.loadImage
import com.datafrey.freymessenger.model.Message
import kotlinx.android.synthetic.main.message_item.view.*

@Suppress("NAME_SHADOWING")
class MessageAdapter(
    context: Activity,
    resource: Int,
    messagesArrayList: List<Message>
) : ArrayAdapter<Message>(context, resource, messagesArrayList) {

    private var activity: Activity = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        val layoutInflater =
            activity.getSystemService(
                Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val message: Message? = getItem(position)

        if (convertView != null)
            viewHolder = convertView.tag as ViewHolder
        else {
            convertView = layoutInflater.inflate(
                R.layout.message_item, parent, false
            )
            viewHolder = ViewHolder(convertView)
            convertView.tag = viewHolder
        }

        val isTextMessage = message?.imageUrl == null

        with (viewHolder) {
            if (isTextMessage) {
                outcomingPhotoImageView.isVisible = false
                incomingPhotoImageView.isVisible = false

                if (message?.isMine!!) {
                    outcomingTextTextView.isVisible = true
                    incomingTextTextView.isVisible = false
                    outcomingTextTextView.text = message.text
                } else {
                    outcomingTextTextView.isVisible = false
                    incomingTextTextView.isVisible = true
                    incomingTextTextView.text = message.text
                }
            } else {
                outcomingTextTextView.isVisible = false
                incomingTextTextView.isVisible = false

                if (message?.isMine!!) {
                    outcomingPhotoImageView.isVisible = true
                    incomingPhotoImageView.isVisible = false
                    context.loadImage(message.imageUrl.toString(), outcomingPhotoImageView)
                } else {
                    outcomingPhotoImageView.isVisible = false
                    incomingPhotoImageView.isVisible = true
                    context.loadImage(message.imageUrl.toString(), incomingPhotoImageView)
                }
            }
        }

        return convertView!!
    }

    private class ViewHolder(view: View) {
        var incomingPhotoImageView: ImageView = view.incomingPhotoImageView
        var incomingTextTextView: TextView = view.incomingTextTextView
        var outcomingPhotoImageView: ImageView = view.outcomingPhotoImageView
        var outcomingTextTextView: TextView = view.outcomingTextTextView
    }
}