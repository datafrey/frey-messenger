package com.datafrey.freymessenger.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.datafrey.freymessenger.R
import com.datafrey.freymessenger.chat.ChatActivity
import com.datafrey.freymessenger.model.User
import com.datafrey.freymessenger.startActivity
import kotlinx.android.synthetic.main.fragment_chats.view.*

class ChatsFragment : Fragment() {

    private lateinit var viewModel: ChatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chats, container, false)

        viewModel = ViewModelProvider(this, ChatsViewModel.ChatsViewModelFactory())
            .get(ChatsViewModel::class.java)

        buildRecyclerView(root.userRecyclerView)

        return root
    }

    private fun buildRecyclerView(userRecyclerView: RecyclerView) {
        val userAdapter = viewModel.getUserAdapter()
        userAdapter.setUserItemEventListener(object : UserItemEventListener {
            override fun onClick(clickedItemUserInfo: User)  {
                context?.startActivity<ChatActivity> {
                    putExtra("recipientUserId", clickedItemUserInfo.id)
                    putExtra("recipientUserName", clickedItemUserInfo.name)
                    putExtra("recipientUserProfilePictureUrl",
                        clickedItemUserInfo.profilePictureUrl)
                }
            }
        })

        userRecyclerView.run {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(
                context, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }
    }

}