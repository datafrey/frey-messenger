package com.datafrey.freymessenger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.loadImage
import com.datafrey.freymessenger.model.User
import kotlinx.android.synthetic.main.user_item.view.*

class UserAdapter(
    private val context: Context,
    var users: List<User>
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private lateinit var listener: OnUserClickListener

    interface OnUserClickListener {
        fun onUserClick(position: Int)
    }

    fun setOnUserClickListener(listener: OnUserClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        UserViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false), listener)

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        with(users[position]) {
            with(holder) {
                if (profilePictureUrl.isNotEmpty())
                    context.loadImage(profilePictureUrl, userIconImageView)
                else
                    userIconImageView.setImageResource(R.drawable.logo)

                userNameTextView.text = name
            }
        }
    }

    class UserViewHolder(
        itemView: View,
        listener: OnUserClickListener
    ) : RecyclerView.ViewHolder(itemView) {
        var userIconImageView = itemView.userIconImageView!!
        var userNameTextView = itemView.userNameTextView!!

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onUserClick(position)
                }
            }
        }
    }
}