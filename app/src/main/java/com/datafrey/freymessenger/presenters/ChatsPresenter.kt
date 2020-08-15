package com.datafrey.freymessenger.presenters

import android.view.View
import com.datafrey.freymessenger.adapters.UserAdapter
import com.datafrey.freymessenger.model.DbNodeNames
import com.datafrey.freymessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChatsPresenter(
    private var view: View?,
    private var users: MutableList<User>
) {

    private val auth = FirebaseAuth.getInstance()
    private val usersDatabaseReference = FirebaseDatabase.getInstance().reference
        .child(DbNodeNames.USERS_DB_NODE_NAME)

    private lateinit var currentUserInfo: User

    private lateinit var userAdapter: UserAdapter

    fun setUserAdapter(userAdapter: UserAdapter) {
        this.userAdapter = userAdapter
    }

    fun getCurrentUserInfo() = currentUserInfo

    fun attachUserDatabaseReferenceListener() {
        usersDatabaseReference.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)

                if (user?.id!! != auth.currentUser?.uid) {
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

}