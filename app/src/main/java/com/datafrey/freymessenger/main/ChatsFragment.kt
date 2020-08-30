package com.datafrey.freymessenger.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.chat.ChatActivity
import com.datafrey.freymessenger.startActivity
import kotlinx.android.synthetic.main.fragment_chats.view.*

class ChatsFragment : Fragment() {

    private lateinit var presenter: ChatsPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chats, container, false)

        presenter = ChatsPresenter(root)

        buildRecyclerView(root.userRecyclerView)

        return root
    }

    private fun buildRecyclerView(userRecyclerView: RecyclerView) {
        val userAdapter = presenter.getUserAdapter()
        userAdapter.setUserItemEventListener(object : UserItemEventListener {
            override fun onClick(position: Int) = goToChat(position)
        })

        userRecyclerView.run {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(
                context, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }
    }

    private fun goToChat(position: Int) {
        val currentUserInfo = presenter.getCurrentUserInfo()
        val users = presenter.getUsers()
        context?.startActivity<ChatActivity> {
            putExtra("recipientUserId", users[position].id)
            putExtra("recipientUserName", users[position].name)
            putExtra("recipientUserProfilePictureUrl", users[position].profilePictureUrl)
            putExtra("senderUserName", currentUserInfo.name)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}