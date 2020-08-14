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
import com.datafrey.freymessenger.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_chats.view.*

class ChatsFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val usersDatabaseReference = FirebaseDatabase.getInstance().reference
        .child("users")

    private lateinit var currentUserInfo: User

    private val users = mutableListOf<User>()
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chats, container, false)

        buildRecyclerView(root.userListRecyclerView)
        attachUserDatabaseReferenceListener()

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
        context?.startActivity<ChatActivity> {
            putExtra("recipientUserId", users[position].id)
            putExtra("recipientUserName", users[position].name)
            putExtra("recipientUserProfilePictureUrl", users[position].profilePictureUrl)
            putExtra("senderUserName", currentUserInfo.name)
        }
    }

    private fun attachUserDatabaseReferenceListener() {
        usersDatabaseReference.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)

                if (user?.id!! != auth.currentUser?.uid) {
                    users.add(user)
                    userAdapter.notifyDataSetChanged()
                } else
                    currentUserInfo = user
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

        })
    }
}