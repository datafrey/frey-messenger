package com.datafrey.freymessenger.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.loadImage
import com.datafrey.freymessenger.model.User
import kotlinx.android.synthetic.main.user_item.view.*

class UserAdapter(
    private val view: View,
    private var users: List<User>
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var userItemEventListener: UserItemEventListener? = null

    fun setUserItemEventListener(userItemEventListener: UserItemEventListener) {
        this.userItemEventListener = userItemEventListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        UserViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false), userItemEventListener)

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        with(users[position]) {
            with(holder) {
                if (profilePictureUrl.isNotEmpty()) {
                    view.context.loadImage(profilePictureUrl, userIconImageView)
                } else {
                    userIconImageView.setImageResource(R.drawable.logo)
                }

                userNameTextView.text = name
            }
        }
    }

    class UserViewHolder(
        itemView: View,
        listener: UserItemEventListener?
    ) : RecyclerView.ViewHolder(itemView) {
        var userIconImageView = itemView.userIconImageView!!
        var userNameTextView = itemView.userNameTextView!!

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onClick(position)
                }
            }
        }
    }

}

interface UserItemEventListener {
    fun onClick(position: Int)
}