package com.datafrey.freymessenger.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.activities.ChatActivity
import com.datafrey.freymessenger.adapters.UserAdapter
import com.datafrey.freymessenger.model.User
import com.datafrey.freymessenger.presenters.ChatsPresenter
import com.datafrey.freymessenger.startActivity
import kotlinx.android.synthetic.main.fragment_chats.view.*

class ChatsFragment : Fragment() {

    private lateinit var presenter: ChatsPresenter

    private val users = mutableListOf<User>()
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chats, container, false)

        presenter = ChatsPresenter(root, users)

        buildRecyclerView(root.userListRecyclerView)

        presenter.setUserAdapter(userAdapter)
        presenter.attachUserDatabaseReferenceListener()

        return root
    }

    private fun buildRecyclerView(userRecyclerView: RecyclerView) {
        userAdapter = UserAdapter(requireContext(), users)
        userAdapter.setOnUserClickListener(object : UserAdapter.OnUserClickListener {
            override fun onUserClick(position: Int) = goToChat(position)
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
        context?.startActivity<ChatActivity> {
            putExtra("recipientUserId", users[position].id)
            putExtra("recipientUserName", users[position].name)
            putExtra("recipientUserProfilePictureUrl", users[position].profilePictureUrl)
            putExtra("senderUserName", currentUserInfo.name)
        }
    }

}