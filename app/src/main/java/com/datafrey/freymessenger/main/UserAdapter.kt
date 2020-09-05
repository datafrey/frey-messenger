package com.datafrey.freymessenger.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.databinding.UserItemBinding
import com.datafrey.freymessenger.model.User

class UserAdapter(
    private var users: List<User>
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var userItemEventListener: UserItemEventListener? = null

    fun setUserItemEventListener(userItemEventListener: UserItemEventListener) {
        this.userItemEventListener = userItemEventListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        UserViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false))

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) =
        holder.bindUser(users[position], userItemEventListener)

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = UserItemBinding.bind(itemView)

        fun bindUser(user: User, userItemEventListener: UserItemEventListener?) {
            binding.user = user
            binding.userItemEventListener = userItemEventListener
        }

    }

}

interface UserItemEventListener {
    fun onClick(clickedItemUserInfo: User)
}