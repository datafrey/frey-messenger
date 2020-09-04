package com.datafrey.freymessenger.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.datafrey.freymessenger.model.DatabaseNodeNames
import com.datafrey.freymessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChatsViewModel : ViewModel() {

    private val usersDatabaseReference = FirebaseDatabase.getInstance()
        .reference.child(DatabaseNodeNames.USERS_DB_NODE_NAME)

    private var users = mutableListOf<User>()
    private var userAdapter = UserAdapter(users)

    fun getUserAdapter() = userAdapter

    init {
        attachUserDatabaseReferenceListener()
    }

    private fun attachUserDatabaseReferenceListener() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        usersDatabaseReference.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(User::class.java)
                if (user?.id!! != currentUserId) {
                    users.add(user)
                    userAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }

    class ChatsViewModelFactory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatsViewModel::class.java)) {
                return ChatsViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}