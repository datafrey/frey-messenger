package com.datafrey.freymessenger.main

import android.view.View
import com.datafrey.freymessenger.model.DatabaseNodeNames
import com.datafrey.freymessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChatsPresenter(private var view: View?) {

    private val usersDatabaseReference = FirebaseDatabase.getInstance()
        .reference.child(DatabaseNodeNames.USERS_DB_NODE_NAME)

    private var users = mutableListOf<User>()
    private var userAdapter = UserAdapter(view!!, users)

    private lateinit var currentUserInfo: User

    init {
        attachUserDatabaseReferenceListener()
    }

    private fun attachUserDatabaseReferenceListener() {
        usersDatabaseReference.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)

                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                if (user?.id!! != currentUserId) {
                    users.add(user)
                    userAdapter.notifyDataSetChanged()
                } else {
                    currentUserInfo = user
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }

    fun getUsers() = users

    fun getUserAdapter() = userAdapter

    fun getCurrentUserInfo() = currentUserInfo

    fun detachView() {
        view = null
    }

}